package com.example.ftpClient.endpoint;

import com.example.ftpClient.service.FtpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ftp")
@Slf4j
public class FtpController {

    private final FtpService ftpService;

    @Autowired
    public FtpController(FtpService ftpService) {
        this.ftpService = ftpService;
    }

    @PostMapping("/file")
    public ResponseEntity<?> sendFile(@RequestBody Map<String, Object> data) {
        try {
            ftpService.sendFile(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An error occurred while sending file to remote server.");
        }
        return ResponseEntity.ok().body("File uploaded");
    }

    @GetMapping("/list")
    public ResponseEntity<?> listFile() {
        try {
            ftpService.listFile();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ResponseEntity.ok().body("Hello world");
    }
}