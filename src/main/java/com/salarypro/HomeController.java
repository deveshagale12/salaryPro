package com.salarypro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")  // Allows frontend to connect
public class HomeController {

	// Use a path relative to your project root
	private final String UPLOAD_DIR = "uploads/home/";

	
	// These would ideally be stored in a database (H2, MySQL) 
    // so they don't reset when you restart the server.
    private String currentHeroTitle = "Welcome to SalaryPro";
    private String currentHeroSubtitle = "The best payroll solution.";
    private String currentAboutText = "SalaryPro was designed to remove the complexity of monthly payroll...";
 // Java Controller check
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private HomeRepository homeRepository;

   @Autowired
    private ContactMessageRepository contactRepo;

    @PostMapping("/contact")
    public ResponseEntity<String> handleContact(@RequestBody Map<String, String> data) {
        try {
            // 1. Map data to Entity
            ContactMessage msg = new ContactMessage();
            msg.setName(data.get("name"));
            msg.setEmail(data.get("email"));
            msg.setSubject(data.get("subject"));
            msg.setMessage(data.get("message"));

            // 2. STORE in Neon Database
            contactRepo.save(msg);

            // 3. SEND Emails
            emailService.sendContactEmail(msg.getEmail(), msg.getName(), msg.getSubject(), msg.getMessage());
            emailService.sendAutoReply(msg.getEmail(), msg.getName());

            return ResponseEntity.ok("Message saved and sent!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/contact/messages")
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        try {
            // Fetches all messages from Neon, sorted by newest first
            List<ContactMessage> messages = contactRepo.findAll();
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/content")
    public ResponseEntity<HomeContent> getHomeContent() {
        // Look in the database for record #1
        return homeRepository.findById(1L)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    // If DB is empty, return a default object so the frontend doesn't crash
                    HomeContent defaultContent = new HomeContent();
                    defaultContent.setTitle("Welcome to SalaryPro");
                    defaultContent.setSubtitle("The best payroll solution.");
                    defaultContent.setAboutText("Default about text...");
                    return ResponseEntity.ok(defaultContent);
                });
    }
    
    @PostMapping("/update-text")
    public ResponseEntity<String> updateText(@RequestBody Map<String, String> data) {
        this.currentHeroTitle = data.get("title");
        this.currentHeroSubtitle = data.get("subtitle");
        return ResponseEntity.ok("Updated");
    }
    @PostMapping("/upload-photos") // Full path: /api/home/upload-photos
    public ResponseEntity<String> uploadHomePhotos(
            @RequestParam("photo1") MultipartFile photo1,
            @RequestParam("photo2") MultipartFile photo2) {
        
        try {
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) directory.mkdirs();

            // Save files
            photo1.transferTo(new File(directory.getAbsolutePath() + File.separator + "banner1.jpg"));
            photo2.transferTo(new File(directory.getAbsolutePath() + File.separator + "banner2.jpg"));

            return ResponseEntity.ok("Photos uploaded successfully!");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }
    @GetMapping("/photo/{filename}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String filename) {
        return homeRepository.findById(1L).map(content -> {
            byte[] imageBytes = filename.contains("1") ? content.getPhoto1() : content.getPhoto2();
            
            if (imageBytes == null) return ResponseEntity.notFound().<byte[]>build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                    .body(imageBytes);
        }).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping("/update-all")
    public ResponseEntity<String> updateAll(
            @RequestParam(value = "photo1", required = false) MultipartFile p1,
            @RequestParam(value = "photo2", required = false) MultipartFile p2,
            @RequestParam("title") String title,
            @RequestParam("subtitle") String subtitle,
            @RequestParam("about") String about) {
        try {
            // 1. Always look for ID 1
            HomeContent content = homeRepository.findById(1L).orElse(new HomeContent());
            
            // 2. CRITICAL: Force the ID to be 1 so it overwrites the existing row
            content.setId(1L); 
            
            content.setTitle(title);
            content.setSubtitle(subtitle);
            content.setAboutText(about);

            if (p1 != null && !p1.isEmpty()) content.setPhoto1(p1.getBytes());
            if (p2 != null && !p2.isEmpty()) content.setPhoto2(p2.getBytes());

            // 3. This will now perform an UPDATE instead of an INSERT
            homeRepository.save(content);
            
            return ResponseEntity.ok("Success - Updated record ID 1");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    
	/*
	 * @PostMapping("/update-all") public ResponseEntity<String> updateAll(
	 * 
	 * @RequestParam("photo1") MultipartFile p1,
	 * 
	 * @RequestParam("photo2") MultipartFile p2,
	 * 
	 * @RequestParam("title") String title,
	 * 
	 * @RequestParam("subtitle") String subtitle,
	 * 
	 * @RequestParam("about") String about) { try { // 1. Update the class variables
	 * this.currentHeroTitle = title; this.currentHeroSubtitle = subtitle;
	 * this.currentAboutText = about; // Save the about text
	 * 
	 * // 2. Ensure the directory exists File directory = new File(UPLOAD_DIR); if
	 * (!directory.exists()) { directory.mkdirs(); }
	 * 
	 * // 3. Use Absolute Paths for transferTo (More stable) File dest1 = new
	 * File(directory.getAbsolutePath() + File.separator + "banner1.jpg"); File
	 * dest2 = new File(directory.getAbsolutePath() + File.separator +
	 * "banner2.jpg");
	 * 
	 * p1.transferTo(dest1); p2.transferTo(dest2);
	 * 
	 * return ResponseEntity.ok("Success"); } catch (Exception e) { // This prints
	 * the real error to your IDE console so you can see it e.printStackTrace();
	 * return ResponseEntity.status(500).body("Error: " + e.getMessage()); } }
	 */
}