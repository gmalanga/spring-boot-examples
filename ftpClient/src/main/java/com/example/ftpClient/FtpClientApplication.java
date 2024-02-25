package com.example.ftpClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;

@SpringBootApplication
@IntegrationComponentScan
public class FtpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(FtpClientApplication.class, args);
    }
}
