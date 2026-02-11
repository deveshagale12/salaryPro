package com.salarypro;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/admin")
public class AdminNoticeController {

    // Simple in-memory storage for the notice
    private Map<String, String> currentNotice = new HashMap<>();

    // GET: Used by the User Dashboard to see the notice
    @GetMapping("/notice")
    public ResponseEntity<Map<String, String>> getNotice() {
        return ResponseEntity.ok(currentNotice);
    }

    // POST: Used by the Admin Dashboard to set the notice
    @PostMapping("/update-notice")
    public ResponseEntity<String> updateNotice(@RequestBody Map<String, String> data) {
        // 'data' will contain {"content": "...", "timestamp": "..."}
        this.currentNotice = data;
        System.out.println("New Notice Posted: " + data.get("content"));
        return ResponseEntity.ok("Notice Updated");
    }
}
