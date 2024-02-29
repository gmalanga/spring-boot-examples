package com.example.ftpClient.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.util.KeyManagerUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway;
import org.springframework.integration.file.remote.session.DelegatingSessionFactory;
import org.springframework.integration.file.support.FileExistsMode;
import org.springframework.integration.ftp.dsl.Ftp;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.DefaultFtpsSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration
@Slf4j
public class GatewayConfiguration {

    @Bean
    DelegatingSessionFactory<FTPFile> dsf(Map<String, DefaultFtpsSessionFactory> ftpsSessionFactories) {
        return new DelegatingSessionFactory<>(ftpsSessionFactories::get);
    }

    @Bean
    DefaultFtpsSessionFactory gary(@Value("${ftp2.user}") String username,
                                  @Value("${ftp2.pass}") String pw,
                                  @Value("${ftp2.host}") String host,
                                  @Value("${ftp2.port}") int port,
                                   @Value("${ftp2.keystore.location}") String keystoreLocation,
                                   @Value("${ftp2.keystore.password}") String keystorePassword) throws GeneralSecurityException, IOException {
        return this.createFtpsSessionFactory(username, pw, host, port, keystoreLocation, keystorePassword);
    }

    @Bean
    DefaultFtpSessionFactory josh(@Value("${ftp1.user}") String username,
                                  @Value("${ftp1.pass}") String pw,
                                  @Value("${ftp1.host}") String host,
                                  @Value("${ftp1.port}") int port,
                                  @Value("${ftp1.client.mode}") int ftpClientMode) {
        return this.createFtpSessionFactory(username, pw, host, port, ftpClientMode);
    }

    private DefaultFtpSessionFactory createFtpSessionFactory(String username, String pw, String host, int port, int ftpClientMode) {
        var defaultFtpSessionFactory = new DefaultFtpSessionFactory();
        defaultFtpSessionFactory.setPassword(pw);
        defaultFtpSessionFactory.setUsername(username);
        defaultFtpSessionFactory.setHost(host);
        defaultFtpSessionFactory.setPort(port);
        defaultFtpSessionFactory.setClientMode(ftpClientMode);
        return defaultFtpSessionFactory;
    }

    private DefaultFtpsSessionFactory createFtpsSessionFactory(String username,
                                                               String pw, String host,
                                                               int port,
                                                               String keystoreLocation,
                                                               String keystorePassword) throws GeneralSecurityException, IOException {
        var defaultFtpsSessionFactory = new DefaultFtpsSessionFactory();
        defaultFtpsSessionFactory.setPassword(pw);
        defaultFtpsSessionFactory.setUsername(username);
        defaultFtpsSessionFactory.setHost(host);
        defaultFtpsSessionFactory.setPort(port);
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
    FtpRemoteFileTemplate ftpRemoteFileTemplate(DelegatingSessionFactory<FTPFile> dsf) {
        var ftpRemoteFileTemplate = new FtpRemoteFileTemplate(dsf);
        ftpRemoteFileTemplate.setRemoteDirectoryExpression(new LiteralExpression(""));
        return ftpRemoteFileTemplate;
    }

    @Bean
    MessageChannel incoming() {
        return MessageChannels.direct().getObject();
    }

    @Bean
    IntegrationFlow gateway(
            FtpRemoteFileTemplate template,
            DelegatingSessionFactory<FTPFile> dsf) {
        return f -> f
                .channel(incoming())
                .handle((key, messageHeaders) -> {
                    dsf.setThreadKey(key);
                    return key;
                })
                .handle(Ftp
                        .outboundGateway(template, AbstractRemoteFileOutboundGateway.Command.PUT, "payload")
                        .fileExistsMode(FileExistsMode.IGNORE)
                        .options(AbstractRemoteFileOutboundGateway.Option.RECURSIVE)
                )
                .handle((key, messageHeaders) -> {
                    dsf.clearThreadKey();
                    return null;
                });
    }

    @Bean
    RouterFunction<ServerResponse> routes() {
        var in = this.incoming();
        return route()
                .POST("/put/{sfn}", request -> {
                    var name = request.pathVariable("sfn");
                    var msg = MessageBuilder.withPayload(name).build();
                    var sent = in.send(msg);
                    return ServerResponse.ok().body(sent);
                })
                .build();
    }
}
