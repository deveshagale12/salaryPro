package com.salarypro;

import jakarta.persistence.*;

@Entity
public class HomeContent {
    @Id
    private Long id = 1L;
    private String title;
    private String subtitle;
    
    @Column(columnDefinition = "TEXT")
    private String aboutText;

    @Lob
    private byte[] photo1;

    @Lob
    private byte[] photo2;

    // --- MANUALLY ADD THESE ---
    public void setTitle(String title) { this.title = title; }
    public String getTitle() { return title; }

    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getSubtitle() { return subtitle; }

    
    public String getAboutText() {
		return aboutText;
	}
	public void setAboutText(String aboutText) {
		this.aboutText = aboutText;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
        this.id = id;
    }
    public void setPhoto1(byte[] photo1) { this.photo1 = photo1; }
    public byte[] getPhoto1() { return photo1; }

    public void setPhoto2(byte[] photo2) { this.photo2 = photo2; }
    public byte[] getPhoto2() { return photo2; }
}