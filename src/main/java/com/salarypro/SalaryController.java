package com.salarypro;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary")
@CrossOrigin("*") // Allows Frontend to talk to Backend
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private SalaryRepository salaryRepository;
    
    // Matches: fetch('/api/salary/add?userId=...&amount=...')
    @PostMapping("/add")
    public ResponseEntity<?> addSalary(
            @RequestParam Long userId, 
            @RequestParam Double amount,
            @RequestParam(required = false) String note) { // Accept note from Frontend
        
        salaryService.addDailySalary(userId, amount, note);
        return ResponseEntity.ok("Salary Added");
    }
    @GetMapping("/all-logs")
    public ResponseEntity<List<SalaryRecord>> getAllLogs() {
        // Now this will work without errors!
        List<SalaryRecord> logs = salaryRepository.findAllByOrderByDateDesc();
        return ResponseEntity.ok(logs);
    }
    @DeleteMapping("/delete-log/{id}")
    public ResponseEntity<?> deleteLog(@PathVariable Long id) {
        salaryService.deleteLog(id);
        return ResponseEntity.ok("Log deleted and balance updated");
    }
    @GetMapping("/logs/filter")
    public ResponseEntity<List<SalaryRecord>> getFilteredLogs(
            @RequestParam int month, 
            @RequestParam int year) {
        return ResponseEntity.ok(salaryRepository.findByMonthAndYear(month, year));
    }
    // Matches: fetch('/api/salary/advance?userId=...&amount=...')
    @PostMapping("/advance")
    public ResponseEntity<?> addAdvance(
            @RequestParam Long userId, 
            @RequestParam Double amount,
            @RequestParam(required = false) String note) { // Accept note from Frontend
            
        salaryService.giveAdvance(userId, amount, note);
        return ResponseEntity.ok("Advance Added");
    }

    // Used by User Dashboard to show the table and totals
    @GetMapping("/my-summary/{userId}")
    public ResponseEntity<?> getSummary(@PathVariable Long userId) {
        return ResponseEntity.ok(salaryService.getUserSalarySummary(userId));
    }
    
    @PostMapping("/pay")
    public ResponseEntity<String> payUser(@RequestParam Long userId, @RequestParam String method) {
        try {
        	salaryService.paySalary(userId, method);         
        	return ResponseEntity.ok("Salary paid and balance cleared!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    
    
}