package com.rangel.financeapi.service;


import com.rangel.financeapi.dto.GoalRequestDTO;
import com.rangel.financeapi.dto.GoalResponseDTO;
import com.rangel.financeapi.model.Category;
import com.rangel.financeapi.model.Goal;
import com.rangel.financeapi.model.User;
import com.rangel.financeapi.repository.CategoryRepository;
import com.rangel.financeapi.repository.GoalRepository;
import com.rangel.financeapi.repository.TransactionRepository;
import com.rangel.financeapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final GoalRepository goalRepository;

    public GoalResponseDTO createGoal(GoalRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category not found");
        }
        if (goalRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                user.getId(), category.getId(), dto.getMonth(), dto.getYear())) {
            throw new RuntimeException("Goal already exists for this category and period");
        }

        Goal goal = Goal.builder()
                .name(dto.getName())
                .user(user)
                .category(category)
                .limitAmount(dto.getLimitAmount())
                .month(dto.getMonth())
                .year(dto.getYear())
                .build();

        Goal saved = goalRepository.save(goal);

        return GoalResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .limitAmount(saved.getLimitAmount())
                .currentAmount(BigDecimal.ZERO)
                .exceeded(false)
                .month(saved.getMonth())
                .year(saved.getYear())
                .categoryId(saved.getCategory().getId())
                .build();
    }

    public List<GoalResponseDTO> getGoalsByUser(String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Goal> goals = goalRepository.findByUserId(user.getId());

        return goals.stream().map(goal -> {
            BigDecimal current = transactionRepository.sumByUserAndCategoryAndPeriod(user.getId(), goal.getCategory().getId(), goal.getMonth(), goal.getYear());
            boolean exceeded = current.compareTo(goal.getLimitAmount()) >= 0;
            return GoalResponseDTO.builder()
                    .id(goal.getId())
                    .name(goal.getName())
                    .limitAmount(goal.getLimitAmount())
                    .currentAmount(current)
                    .exceeded(exceeded)
                    .month(goal.getMonth())
                    .year(goal.getYear())
                    .categoryId(goal.getCategory().getId())
                    .build();
                }).toList();
    }

    public GoalResponseDTO getGoalById(Long id, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Goal not found");
        }
        BigDecimal current = transactionRepository.sumByUserAndCategoryAndPeriod(
                user.getId(), goal.getCategory().getId(), goal.getMonth(), goal.getYear());
        boolean exceeded = current.compareTo(goal.getLimitAmount()) >= 0;
        return GoalResponseDTO.builder()
                .id(goal.getId())
                .name(goal.getName())
                .limitAmount(goal.getLimitAmount())
                .currentAmount(current)
                .exceeded(exceeded)
                .month(goal.getMonth())
                .year(goal.getYear())
                .categoryId(goal.getCategory().getId())
                .build();
    }

    public void deleteGoal(Long id, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Goal not found");
        }
        goalRepository.delete(goal);
    }

    public GoalResponseDTO updateGoal(Long id, GoalRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Goal not found");
        }
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Category not found");
        }
        if (goalRepository.existsDuplicateGoal(user.getId(), category.getId(), dto.getMonth(), dto.getYear(), id)) {
            throw new RuntimeException("Goal already exists for this category and period");
        }

        goal.setName(dto.getName());
        goal.setLimitAmount(dto.getLimitAmount());
        goal.setMonth(dto.getMonth());
        goal.setYear(dto.getYear());
        goal.setCategory(category);
        goalRepository.save(goal);

        BigDecimal current = transactionRepository.sumByUserAndCategoryAndPeriod(
                user.getId(), category.getId(), goal.getMonth(), goal.getYear());
        boolean exceeded = current.compareTo(goal.getLimitAmount()) >= 0;

        return GoalResponseDTO.builder()
                .id(goal.getId())
                .name(goal.getName())
                .limitAmount(goal.getLimitAmount())
                .currentAmount(current)
                .exceeded(exceeded)
                .month(goal.getMonth())
                .year(goal.getYear())
                .categoryId(goal.getCategory().getId())
                .build();
    }

    public void generateDefaultGoalsForAllUsers() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        String monthName = Month.of(month).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        String capitalizedMonthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);

        BigDecimal defaultLimitAmount = new BigDecimal("300.00");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Category> categories = categoryRepository.findByUserId(user.getId());

            for (Category category : categories) {
                try {
                    boolean alreadyExists = goalRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                            user.getId(), category.getId(), month, year
                    );

                    if (!alreadyExists) {
                        Goal goal = Goal.builder()
                                .name(category.getName() + " em " + capitalizedMonthName)
                                .user(user)
                                .category(category)
                                .limitAmount(defaultLimitAmount)
                                .month(month)
                                .year(year)
                                .build();

                        goalRepository.save(goal);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to generate automatic goal for user " + user.getId()
                            + " and category " + category.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}