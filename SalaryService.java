package com.salarypro;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import jakarta.transaction.Transactional;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private UserRepository userRepository;

    /**
     * FEATURE 1: Add Daily Salary (Admin Action)
     * Tags record as "SALARY" and updates User's running total.
     */
    @Transactional
    public void addDailySalary(Long userId, Double amount, String note) { // Added 'String note' here
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update user totals
        user.setTotalEarned(user.getTotalEarned() + amount);
        userRepository.save(user);

        // Create the record
        SalaryRecord record = new SalaryRecord();
        record.setDailyAmount(amount);
        record.setType("SALARY");
        record.setNote(note); // Use the variable passed into the method
        record.setDate(LocalDate.now());
        record.setUser(user);
        
        salaryRepository.save(record);
    }

    /**
     * FEATURE 2: Give Advance (Admin Action)
     * Tags record as "ADVANCE" and updates User's advance total.
     */
    @Transactional
    public void giveAdvance(Long userId, double amount, String note) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Update the total advance taken in the User table
        user.setAdvanceAmount(user.getAdvanceAmount() + amount);
        userRepository.save(user);

        // 2. Create a History Record tagged as ADVANCE
        SalaryRecord record = new SalaryRecord();
        record.setDailyAmount(amount);
        record.setType("ADVANCE"); 
        record.setNote(note);
        record.setDate(LocalDate.now());
        record.setUser(user);
        
        salaryRepository.save(record);
    }

    /**
     * FEATURE 3: Get Complete Summary (User Dashboard)
     * Calculates totals and provides the history list.
     */
    public Map<String, Object> getUserSalarySummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<SalaryRecord> records = salaryRepository.findByUserId(userId);
        
        // Use the fields directly from the User entity for accuracy
        double totalEarned = user.getTotalEarned();
        double totalAdvance = user.getAdvanceAmount();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("userName", user.getName());
        summary.put("dailyRecords", records); // Full history for the table
        summary.put("totalEarned", totalEarned);
        summary.put("advanceTaken", totalAdvance);
        summary.put("netPayable", totalEarned - totalAdvance);
        
        return summary;
    }

    /**
     * FEATURE 4: Monthly Filter (Bonus)
     * Gets records for a specific month only.
     */
    public List<SalaryRecord> getMonthlyRecords(Long userId, int month, int year) {
        // Logic to filter records by month and year
        return salaryRepository.findByUserId(userId).stream()
                .filter(r -> r.getDate().getMonthValue() == month && r.getDate().getYear() == year)
                .toList();
    }
    
    @Transactional
    public void paySalary(Long userId, String method) { // Added 'method' parameter
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        double netPayable = user.getTotalEarned() - user.getAdvanceAmount();
        
        if (netPayable <= 0) {
            throw new RuntimeException("No balance to pay!");
        }

        // 1. Create a Record of the Final Payment
        SalaryRecord record = new SalaryRecord();
        record.setDailyAmount(netPayable);
        record.setType("PAYMENT"); // Explicitly setting the type
        
        // Use the 'method' (CASH/ONLINE) as the note so it shows in the logs
        record.setNote("Final Settlement via " + method); 
        
        record.setDate(LocalDate.now());
        record.setUser(user);
        salaryRepository.save(record);

        // 2. DEDUCTION LOGIC: Reset balances after payment
        user.setTotalEarned(0.0);
        user.setAdvanceAmount(0.0);
        userRepository.save(user);
    }
    @Transactional
    public void deleteLog(Long logId) {
        SalaryRecord record = salaryRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        
        User user = record.getUser();
        Double amount = record.getDailyAmount();

        // Reverse the balance math based on transaction type
        if ("SALARY".equals(record.getType())) {
            // If we delete a salary credit, decrease their earnings
            user.setTotalEarned(user.getTotalEarned() - amount);
        } 
        else if ("ADVANCE".equals(record.getType())) {
            // If we delete an advance debit, decrease their debt
            user.setAdvanceAmount(user.getAdvanceAmount() - amount);
        } 
        else if ("PAYMENT".equals(record.getType())) {
            // NEW: If we delete a settlement, we restore the 'Total Earned' 
            // so the admin can try the payment again.
            user.setTotalEarned(user.getTotalEarned() + amount);
            // Note: We assume the advance was already 0 after payment, 
            // so restoring the amount to Earned makes the Net Balance correct again.
        }

        userRepository.save(user);
        salaryRepository.delete(record);
    }
}