package com.example.campus_backend.controller;

import com.example.campus_backend.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    private static final Set<String> ALLOWED = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请选择图片");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot).toLowerCase();
        }
        if (!ALLOWED.contains(ext)) {
            throw new IllegalArgumentException("只支持 jpg、png、webp、gif 图片");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只能上传图片文件");
        }

        Path dir = Paths.get(System.getProperty("user.dir"), "uploads");
        Files.createDirectories(dir);
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        file.transferTo(dir.resolve(filename).toFile());
        return Result.success("/api/files/" + filename);
    }
}
