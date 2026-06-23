package com.rangel.financeapi.service;

import com.rangel.financeapi.dto.CategoryRequestDTO;
import com.rangel.financeapi.dto.CategoryResponseDTO;
import com.rangel.financeapi.model.Category;
import com.rangel.financeapi.model.User;
import com.rangel.financeapi.repository.CategoryRepository;
import com.rangel.financeapi.repository.TransactionRepository;
import com.rangel.financeapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_success() {
        String email = "test@email.com";
        User user = User.builder().id(1L).email(email).build();
        CategoryRequestDTO dto = new CategoryRequestDTO("Alimentação", "#003366");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.save(any())).thenAnswer(i -> {
            Category c = i.getArgument(0);
            return Category.builder()
                    .id(1L)
                    .name(c.getName())
                    .color(c.getColor())
                    .user(c.getUser())
                    .build();
        });

        CategoryResponseDTO response = categoryService.createCategory(dto, email);

        assertThat(response.getName()).isEqualTo("Alimentação");
        assertThat(response.getColor()).isEqualTo("#003366");
    }

    @Test
    void createCategory_userNotFound() {
        String email = "notfound@email.com";
        CategoryRequestDTO dto = new CategoryRequestDTO("Alimentação", "#003366");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.createCategory(dto, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void deleteCategory_success() {
        String email = "test@email.com";
        User user = User.builder().id(1L).email(email).build();
        Category category = Category.builder().id(1L).name("Alimentação").color("#003366").user(user).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory(1L, email);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_notOwner() {
        String email = "test@email.com";
        User user = User.builder().id(1L).email(email).build();
        User otherUser = User.builder().id(2L).email("other@email.com").build();
        Category category = Category.builder().id(1L).name("Alimentação").color("#003366").user(otherUser).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.deleteCategory(1L, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");
    }

    @Test
    void deleteCategory_hasTransactions() {
        String email = "test@email.com";
        User user = User.builder().id(1L).email(email).build();
        Category category = Category.builder().id(1L).name("Alimentação").color("#003366").user(user).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L, email))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category has transactions and cannot be deleted");
    }
}
