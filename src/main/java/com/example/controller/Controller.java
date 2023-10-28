package com.example.controller;

import com.example.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController("/api")
public class Controller {

    private final FileService service;

    public Controller(FileService service) {
        this.service = service;
    }

    @GetMapping("/response")
    public ResponseEntity<String> responseFromAPI() {
        try {
            String response = service.getURL();
            return ResponseEntity.ok(response);
        } catch (IOException exception) {
            exception.printStackTrace();
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PutMapping("/upload")
    public ResponseEntity<String> uploadFile() {
        try {
            String response = service.uploadFile();
            return ResponseEntity.ok(response);
        } catch (IOException exception) {
            exception.printStackTrace();
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile() {
        try {
            String response = service.deleteFile();
            return ResponseEntity.ok(response);
        } catch (IOException exception) {
            exception.printStackTrace();
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @PutMapping("/public")
    public ResponseEntity<String> makeFilePublic() {
        try {
            String response = service.makeFilePublic();
            return ResponseEntity.ok(response);
        } catch (IOException exception) {
            exception.printStackTrace();
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }
}
