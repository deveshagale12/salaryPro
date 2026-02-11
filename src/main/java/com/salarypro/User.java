package com.salarypro;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String mobile;
    
    private double totalEarned;
    
 // New fields for financial tracking
    private double advanceAmount; 
    private String role; // "ADMIN" or "USER"

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getTotalEarned() {
		return totalEarned;
	}

	public void setTotalEarned(double totalEarned) {
		this.totalEarned = totalEarned;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public double getAdvanceAmount() {
		return advanceAmount;
	}

	public void setAdvanceAmount(double advanceAmount) {
		this.advanceAmount = advanceAmount;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
    
    }