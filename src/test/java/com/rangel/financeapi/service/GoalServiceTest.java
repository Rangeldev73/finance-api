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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    @Test
    void createGoal_success() {
        String email = "test@email.com";
        User user = User.builder().id(1L).email(email).build();
        Category category = Category.builder().id(1L).name("Alimentação").color("#003366").user(user).build();
        GoalRequestDTO dto = new GoalRequestDTO("Meta Janeiro", BigDecimal.valueOf(500), 1L, 6, 2026);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(goalRepository.existsByUserIdAndCategoryIdAndMonthAndYear(1L, 1L, 6, 2026)).thenReturn(false);
        when(goalRepository.save(any())).thenAnswer(i -> {
            Goal g = i.getArgument(0);
            return Goal.builder()
                    .id(1L)
                    .name(g.getName())
                    .limitAmount(g.getLimitAmount())
                    .month(g.getMonth())
                    .year(g.getYear())
                    .user(g.getUser())
                    .category(g.getCategory())
                    .build();
        });

        GoalResponseDTO response = goalService.createGoal(dto, email);

        assertThat(response.getName()).isEqualTo("Meta Janeiro");
        assertThat(response.getLimitAmount()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(response.getCategoryId()).isEqualTo(1L);
    }

    @Test
    void createGoal_userNotFound() {
        String email = "notfound@email.com";
        GoalRequestDTO dto = new GoalRequestDTO("Meta Janeiro", BigDecimal.valueOf(500), 1L, 6, 2026);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> goalService.createGoal(dto, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void createGoal_duplicate() {
        String email = "test@email.com";
        User user = User.builder().id(1L).email(email).build();
        Category category = Category.builder().id(1L).name("Alimentação").color("#003366").user(user).build();
        GoalRequestDTO dto = new GoalRequestDTO("Meta Janeiro", BigDecimal.valueOf(500), 1L, 6, 2026);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(goalRepository.existsByUserIdAndCategoryIdAndMonthAndYear(1L, 1L, 6, 2026)).thenReturn(true);

        assertThatThrownBy(() -> goalService.createGoal(dto, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Goal already exists for this category and period");
    }

    @Test
    void getGoalsByUser_success() {
        String email = "test@email.com";
        User user = User.builder().id(1L).email(email).build();
        Category category = Category.builder().id(1L).name("Alimentação").color("#003366").user(user).build();
        Goal goal = Goal.builder().id(1L).name("Meta Janeiro").limitAmount(BigDecimal.valueOf(500))
                .month(6).year(2026).user(user).category(category).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(goalRepository.findByUserId(1L)).thenReturn(List.of(goal));
        when(transactionRepository.sumByUserAndCategoryAndPeriod(1L, 1L, 6, 2026))
                .thenReturn(BigDecimal.valueOf(81.40));

        List<GoalResponseDTO> response = goalService.getGoalsByUser(email);

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getCurrentAmount()).isEqualByComparingTo(BigDecimal.valueOf(81.40));
        assertThat(response.get(0).isExceeded()).isFalse();
    }
}