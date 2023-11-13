package com.example.service;

import com.example.models.dto.DiskFileDTO;

import java.io.File;

public interface DiskService {
    DiskFileDTO upload(File file);

    void delete(String diskFilePath);
}
