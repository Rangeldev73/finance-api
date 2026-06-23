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
import java.util.List;

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
}
