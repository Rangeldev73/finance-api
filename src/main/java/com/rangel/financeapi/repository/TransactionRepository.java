package com.rangel.financeapi.repository;

import com.rangel.financeapi.dto.TransactionResponseDTO;
import com.rangel.financeapi.model.Transaction;
import com.rangel.financeapi.model.Type;
import com.rangel.financeapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


public interface TransactionRepository  extends JpaRepository<Transaction,  Long> {
    List<Transaction> findByUserId(Long id);

    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumByUserAndType(@Param("userId") Long userId, @Param("type") Type type);

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    List<Transaction> findByUserAndCreatedAtBetween(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId AND MONTH(t.createdAt) = :month AND YEAR(t.createdAt) = :year")
    BigDecimal sumByUserAndCategoryAndPeriod(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("month") int month, @Param("year") int year);
}
