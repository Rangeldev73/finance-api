package com.rangel.financeapi.repository;

import com.rangel.financeapi.model.Transaction;
import com.rangel.financeapi.model.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;


public interface TransactionRepository  extends JpaRepository<Transaction,  Long> {
    List<Transaction> findByUserId(Long id);

    List<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type")
    BigDecimal sumByUserAndType(@Param("userId") Long userId, @Param("type") Type type);

    boolean existsByCategoryId(Long categoryId);
}
