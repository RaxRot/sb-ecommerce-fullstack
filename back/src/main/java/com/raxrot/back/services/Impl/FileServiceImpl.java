package com.raxrot.back.services.Impl;

import com.raxrot.back.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile image) throws IOException {
        log.info("Storing image in folder: {}", path);

        String originalFilename = image.getOriginalFilename();
        log.debug("Original filename: {}", originalFilename);

        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf(".")));
        String filePath = path + File.separator + fileName;

        File folder = new File(path);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            log.info("Image folder created: {}, success={}", folder.getAbsolutePath(), created);
        }

        try {
            Files.copy(image.getInputStream(), Paths.get(filePath));
            log.info("Image successfully saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save image to {}", filePath, e);
            throw e;
        }

        return fileName;
    }
}
