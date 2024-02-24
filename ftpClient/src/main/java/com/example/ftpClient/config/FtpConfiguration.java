package com.example.ftpClient.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.ftp.gateway.FtpOutboundGateway;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizer;
import org.springframework.integration.ftp.inbound.FtpInboundFileSynchronizingMessageSource;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;

@Configuration
@Slf4j
public class FtpConfiguration {

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.user}")
    private String user;

    @Value("${ftp.pass}")
    private String pass;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.client.mode}")
    private int ftpClientMode;

    @Value("${ftp.remote.directory}")
    private String ftpRemoteDirectory;

    @Value("${ftp.local.directory}")
    private String ftpLocalDirectory;

    @Bean
    public SessionFactory<FTPFile> ftpSessionFactory() {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(user);
        factory.setPassword(pass);
        factory.setClientMode(ftpClientMode);
        return new CachingSessionFactory<>(factory);
    }

    @Bean
    public FtpInboundFileSynchronizer ftpInboundFileSynchronizer() {
        FtpInboundFileSynchronizer fileSynchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
        fileSynchronizer.setDeleteRemoteFiles(false);
        fileSynchronizer.setRemoteDirectory(ftpRemoteDirectory);
        return fileSynchronizer;
    }

    @Bean
    @InboundChannelAdapter(channel = "fromFtpChannel", poller = @Poller(fixedDelay = "5000"))
    public MessageSource<File> ftpMessageSource() {
        FtpInboundFileSynchronizingMessageSource source =
                new FtpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizer());
        source.setLocalDirectory(new File(ftpLocalDirectory));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new AcceptOnceFileListFilter<File>());
        source.setMaxFetchSize(1);
        return source;
    }

    @Bean
    @ServiceActivator(inputChannel = "fromFtpChannel")
    public MessageHandler handlerFrom() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                log.info(message.getPayload().toString());
            }
        };
    }

    @Bean
    @ServiceActivator(inputChannel = "toFtpChannel")
    public MessageHandler handler() {
        FtpOutboundGateway ftpOutboundGateway =
                new FtpOutboundGateway(ftpSessionFactory(), "put", null);
//        ftpOutboundGateway.setOutputChannelName("lsReplyChannel");
        ftpOutboundGateway.setRemoteDirectoryExpression(new LiteralExpression(ftpRemoteDirectory));
        return ftpOutboundGateway;
    }
}
