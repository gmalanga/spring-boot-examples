package com.example.ftpsServer;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.ClientAuth;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class FtpsServerApplication {

	private static final List<Authority> ADMIN_AUTHORITIES;
	public static final int MAX_CONCURRENT_LOGINS = 1;
	public static final int MAX_CONCURRENT_LOGINS_PER_IP = 1;
	private static final String DEFAULT_USER_DIR = "src/main/resources/ftps/remote";

	static {
		// Admin authorities
		ADMIN_AUTHORITIES = new ArrayList<>();
		ADMIN_AUTHORITIES.add(new WritePermission());
		ADMIN_AUTHORITIES.add(new ConcurrentLoginPermission(MAX_CONCURRENT_LOGINS, MAX_CONCURRENT_LOGINS_PER_IP));
		ADMIN_AUTHORITIES.add(new TransferRatePermission(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	public static void main(String[] args) throws FtpException {
		SpringApplication.run(FtpsServerApplication.class, args);

		// FTPS Embedded server
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = getListenerFactory();

		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		UserManager userManager = userManagerFactory.createUserManager();
		serverFactory.setUserManager(userManager);

		// Create admin user
		BaseUser adminUser = new BaseUser();
		adminUser.setName("demo");
		adminUser.setPassword("secret1234");
		adminUser.setEnabled(true);
		adminUser.setAuthorities(ADMIN_AUTHORITIES);
		adminUser.setHomeDirectory(DEFAULT_USER_DIR);
		adminUser.setMaxIdleTime(0);
		serverFactory.getUserManager().save(adminUser);

		// replace the default listener
		serverFactory.addListener("default", factory.createListener());

		// start the server
		FtpServer server = serverFactory.createServer();
		server.start();
	}

	private static ListenerFactory getListenerFactory() {
		ListenerFactory factory = new ListenerFactory();

		// set the port of the listener
		factory.setPort(9900);

		// define SSL configuration
		SslConfigurationFactory ssl = new SslConfigurationFactory();
		ssl.setKeystoreFile(new File("src/main/resources/ftps/certs/domain.jks"));
		ssl.setKeystorePassword("password");
		ssl.setClientAuthentication(String.valueOf(ClientAuth.WANT));

		// set the SSL configuration for the listener
		factory.setSslConfiguration(ssl.createSslConfiguration());
		factory.setImplicitSsl(true);
		return factory;

	}
}
