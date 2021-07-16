package com.runmate.service.user;

import com.amazonaws.services.s3.model.PutObjectRequest;
import com.runmate.controller.exception.ImageFileUploadFailedException;
import com.runmate.domain.user.ImageFileInfo;
import com.runmate.domain.user.User;
import com.runmate.exception.NotFoundUserEmailException;
import com.runmate.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import com.amazonaws.services.s3.AmazonS3Client;

@Service
@RequiredArgsConstructor
@Transactional
public class UserImageUploadService {
    private final UserRepository userRepository;
    private final AmazonS3Client amazonS3Client;
    private static final String exceptionMessage = "upload failed";
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String saveImage(MultipartFile targetFile, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(NotFoundUserEmailException::new);
        LocalDateTime createdAt = LocalDateTime.now();
        String fileObjectKey = makeObjectKey(createdAt, userEmail, targetFile.getOriginalFilename());
        try {
            Path savedFile = convertIntoFileInLocal(targetFile);

            checkFileTypeIsImage(savedFile);
            saveToS3(savedFile, fileObjectKey);
            deleteFile(savedFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ImageFileUploadFailedException(exceptionMessage);
        }

        ImageFileInfo imageFileInfo = ImageFileInfo.of()
                .keyName(fileObjectKey)
                .createdAt(createdAt)
                .build();
        user.selectImage(imageFileInfo);
        return fileObjectKey;
    }

    private void saveToS3(Path targetFile, String key) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, key, targetFile.toFile()));
    }

    private String makeObjectKey(LocalDateTime current, String userEmail, String fileName) {
        return current.toString() + "/" + userEmail + "/" + fileName;
    }

    private Path convertIntoFileInLocal(MultipartFile targetFile) throws IOException {
        Path temporaryFile = Paths.get("/tmp/" + targetFile.getOriginalFilename());
        targetFile.transferTo(temporaryFile);
        return temporaryFile;
    }

    private void deleteFile(Path path) throws IOException {
        Files.delete(path);
    }

    private void checkFileTypeIsImage(Path targetFile) throws IOException {
        String mimeType = Files.probeContentType(targetFile);
        if (!mimeType.startsWith("image"))
            throw new IllegalArgumentException("not image file");
    }
}
