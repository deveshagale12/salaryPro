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

    @PostMapping("/contact")
    public ResponseEntity<String> handleContact(@RequestBody Map<String, String> data) {
        try {
        	String name = data.get("name");
            String email = data.get("email");
            String subject = data.get("subject");
            String message = data.get("message");

            // Step 1: Notify the Admin (You)
            emailService.sendContactEmail(email, name, data.get("subject"), data.get("message"));

            // Step 2: Send Auto-Reply to User
            emailService.sendAutoReply(email, name);

            return ResponseEntity.ok("Messages processed successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/content")
    public Map<String, String> getHomeContent() {
        Map<String, String> data = new HashMap<>();
        data.put("title", this.currentHeroTitle);
        data.put("subtitle", this.currentHeroSubtitle);
        data.put("aboutText", this.currentAboutText); // Make sure this variable was updated in update-all
        return data;
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
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {
        try {
            // 1. Define the path to the uploads folder
            Path filePath = Paths.get("uploads/home/").resolve(filename).normalize();
            
            // 2. Create the UrlResource
            Resource resource = new UrlResource(filePath.toUri());

            // 3. Check if file exists and is readable
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @PostMapping("/update-all")
    public ResponseEntity<String> updateAll(
            @RequestParam("photo1") MultipartFile p1,
            @RequestParam("photo2") MultipartFile p2,
            @RequestParam("title") String title,
            @RequestParam("subtitle") String subtitle,
            @RequestParam("about") String about) {
        try {
            // 1. Update the class variables
            this.currentHeroTitle = title;
            this.currentHeroSubtitle = subtitle;
            this.currentAboutText = about; // Save the about text

            // 2. Ensure the directory exists
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs(); 
            }

            // 3. Use Absolute Paths for transferTo (More stable)
            File dest1 = new File(directory.getAbsolutePath() + File.separator + "banner1.jpg");
            File dest2 = new File(directory.getAbsolutePath() + File.separator + "banner2.jpg");

            p1.transferTo(dest1);
            p2.transferTo(dest2);

            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            // This prints the real error to your IDE console so you can see it
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}