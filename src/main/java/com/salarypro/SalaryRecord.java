package com.salarypro;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
public class SalaryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String note;
    private Double dailyAmount;
    private LocalDate date;
    
    private String type; // Will store "SALARY" or "ADVANCE"
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Default Constructor
    public SalaryRecord() {}

    // Convenience Constructor for Service Layer
    public SalaryRecord(Double dailyAmount, LocalDate date, User user) {
        this.dailyAmount = dailyAmount;
        this.date = date;
        this.user = user;
    }

    // --- Standard Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getDailyAmount() { return dailyAmount; }
    public void setDailyAmount(Double dailyAmount) { this.dailyAmount = dailyAmount; }

    // ALIAS METHOD: This fixes the "setAmount is undefined" error in your Service
    public void setAmount(Double amount) {
        this.dailyAmount = amount;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    
}