package com.example.ftpClient;

import com.example.ftpClient.core.UploadGateway;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.integration.annotation.IntegrationComponentScan;

import java.io.File;

@SpringBootApplication
@IntegrationComponentScan
public class FtpClientApplication {

    public static void main(String[] args) {

//		SpringApplication.run(FtpClientApplication.class, args);

        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(FtpClientApplication.class)
                        .run(args);

        UploadGateway uploadGateway = context.getBean(UploadGateway.class);
        uploadGateway.sendToFtp(new File("src/main/resources/bar.txt"));
    }
}
