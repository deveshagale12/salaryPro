package com.salarypro;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends JpaRepository<SalaryRecord, Long> {
    
    // This finds all daily salary entries for a specific user
    List<SalaryRecord> findByUserId(Long userId);
 // Add this line to fix the "undefined" error
    List<SalaryRecord> findAllByOrderByDateDesc();
    
    // This helps if you ever want to find logs for just one specific user
    List<SalaryRecord> findByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT s FROM SalaryRecord s WHERE FUNCTION('MONTH', s.date) = :month AND FUNCTION('YEAR', s.date) = :year ORDER BY s.date DESC")
    List<SalaryRecord> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}