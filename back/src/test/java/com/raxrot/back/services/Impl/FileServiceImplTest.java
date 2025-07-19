package com.raxrot.back.services.Impl;

import com.raxrot.back.services.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension .class)
class FileServiceImplTest {

    private FileService fileService = new FileServiceImpl();

    @TempDir
    Path tempDir;

    @Test
    void shouldUploadImageSuccessfully() throws IOException {
        // given
        MockMultipartFile image = new MockMultipartFile(
                "file", "test.png", "image/png", "fake-image".getBytes()
        );

        // when
        String savedFileName = fileService.uploadImage(tempDir.toString(), image);

        // then
        Path savedPath = tempDir.resolve(savedFileName);
        assertTrue(Files.exists(savedPath));
    }
}