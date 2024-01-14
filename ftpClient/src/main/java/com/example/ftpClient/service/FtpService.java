package com.example.ftpClient.service;

import com.example.ftpClient.core.CsvFileGenerator;
import com.example.ftpClient.core.UploadGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
@Slf4j
public class FtpService {

    private final UploadGateway uploadGateway;
    private final CsvFileGenerator csvFileGenerator;

    @Autowired
    public FtpService(UploadGateway uploadGateway,
                      CsvFileGenerator csvFileGenerator) {
        this.uploadGateway = uploadGateway;
        this.csvFileGenerator = csvFileGenerator;
    }

    public void sendFile(Map<String, Object> data) {
        try {
            File file = csvFileGenerator.createFile(data);
            uploadGateway.upload(file);
            log.info("File {} successfully uploaded to remote ftp server.", file.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            csvFileGenerator.eraseFile();
        }
    }

    public String listFile() {
        return "";
    }
}
