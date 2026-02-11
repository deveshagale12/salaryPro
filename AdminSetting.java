package com.salarypro;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class AdminSetting {
    @Id
    private String settingKey = "GLOBAL_NOTICE";
    private String settingValue;
	public String getSettingKey() {
		return settingKey;
	}
	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}
	public String getSettingValue() {
		return settingValue;
	}
	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}

    // Getters and Setters
}