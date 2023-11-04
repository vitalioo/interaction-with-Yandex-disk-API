package com.example.controller;

import com.example.service.DiskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/yandex_disk_api")
public class Controller {

    private final DiskService service;

    public Controller(DiskService service) {
        this.service = service;
    }

    @GetMapping("/upload")
    public ResponseEntity<String> uploadFile() {
        String response = service.upload();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile() {
        String response = service.delete();
        return ResponseEntity.ok(response);
    }
}
