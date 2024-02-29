package com.example.ftpClient.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.util.KeyManagerUtils;
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
import org.springframework.integration.ftp.session.DefaultFtpsSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
@Slf4j
public class FtpConfiguration {

    @Value("${ftp1.host}")
    private String host1;
    @Value("${ftp1.user}")
    private String user1;
    @Value("${ftp1.pass}")
    private String pass1;
    @Value("${ftp1.port}")
    private int port1;
    @Value("${ftp1.client.mode}")
    private int ftpClientMode1;
    @Value("${ftp1.remote.directory}")
    private String ftpRemoteDirectory1;
    @Value("${ftp1.local.directory}")
    private String ftpLocalDirectory1;
    @Value("${ftp2.host}")
    private String host2;
    @Value("${ftp2.user}")
    private String user2;
    @Value("${ftp2.pass}")
    private String pass2;
    @Value("${ftp2.port}")
    private int port2;
    @Value("${ftp2.client.mode}")
    private int ftpClientMode2;
    @Value("${ftp2.remote.directory}")
    private String ftpRemoteDirectory2;
    @Value("${ftp2.local.directory}")
    private String ftpLocalDirectory2;
    @Value("${ftp2.keystore.location}")
    private String keystoreLocation;
    @Value("${ftp2.keystore.password}")
    private String keystorePassword;

    @Bean
    public SessionFactory<FTPFile> ftpSessionFactory() {
        DefaultFtpSessionFactory factory = new DefaultFtpSessionFactory();
        factory.setHost(host1);
        factory.setPort(port1);
        factory.setUsername(user1);
        factory.setPassword(pass1);
        factory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
        return new CachingSessionFactory<>(factory);
    }

    @Bean
    public SessionFactory<FTPFile> ftpsSessionFactory() throws GeneralSecurityException, IOException {
        var defaultFtpsSessionFactory = new DefaultFtpsSessionFactory();
        defaultFtpsSessionFactory.setPassword(pass2);
        defaultFtpsSessionFactory.setUsername(user2);
        defaultFtpsSessionFactory.setHost(host2);
        defaultFtpsSessionFactory.setPort(port2);
        defaultFtpsSessionFactory.setClientMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
        defaultFtpsSessionFactory.setProtocol("TLS");
        defaultFtpsSessionFactory.setImplicit(true);
        defaultFtpsSessionFactory.setKeyManager(KeyManagerUtils.createClientKeyManager(
                new File(keystoreLocation),
                keystorePassword
        ));
        return defaultFtpsSessionFactory;
    }

    @Bean
    public FtpInboundFileSynchronizer ftpInboundFileSynchronizer() {
        FtpInboundFileSynchronizer fileSynchronizer = new FtpInboundFileSynchronizer(ftpSessionFactory());
        fileSynchronizer.setDeleteRemoteFiles(false);
        fileSynchronizer.setRemoteDirectory(ftpRemoteDirectory1);
        return fileSynchronizer;
    }

    @Bean
    @InboundChannelAdapter(channel = "fromFtpChannel", poller = @Poller(fixedDelay = "5000"))
    public MessageSource<File> ftpMessageSource() {
        FtpInboundFileSynchronizingMessageSource source =
                new FtpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizer());
        source.setLocalDirectory(new File(ftpLocalDirectory1));
        source.setAutoCreateLocalDirectory(true);
        source.setLocalFilter(new AcceptOnceFileListFilter<>());
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
        ftpOutboundGateway.setRemoteDirectoryExpression(new LiteralExpression(ftpRemoteDirectory1));
        return ftpOutboundGateway;
    }

    @Bean
    @ServiceActivator(inputChannel = "toFtpsChannel")
    public MessageHandler handlerFtps() throws GeneralSecurityException, IOException {
        FtpOutboundGateway ftpOutboundGateway =
                new FtpOutboundGateway(ftpsSessionFactory(), "put", null);
        ftpOutboundGateway.setRemoteDirectoryExpression(new LiteralExpression(ftpRemoteDirectory2));
        return ftpOutboundGateway;
    }
}
