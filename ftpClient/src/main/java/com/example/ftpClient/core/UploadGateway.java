package com.example.ftpClient.core;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@MessagingGateway
public interface UploadGateway {
    //    @Gateway(requestChannel = "ftpChannel")
//    void upload(File file);
    @Gateway(requestChannel = "toFtpChannel")
    void sendToFtp(File file);
}
