package com.rangel.financeapi.repository;

import com.rangel.financeapi.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);

    boolean existsByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, int month, int year);

    @Query("SELECT COUNT(g) > 0 FROM Goal g WHERE g.user.id = :userId AND g.category.id = :categoryId AND g.month = :month AND g.year = :year AND g.id <> :excludeId")
    boolean existsDuplicateGoal(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("month") int month, @Param("year") int year, @Param("excludeId") Long excludeId);
}
