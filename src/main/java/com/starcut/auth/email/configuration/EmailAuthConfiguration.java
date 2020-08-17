package com.starcut.auth.email.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.starcut.auth.common.RoleProviderI;
import com.starcut.auth.common.db.DummyRoleProvider;
import com.starcut.auth.common.db.DummyUserRepository;
import com.starcut.auth.common.db.EmailUserRepositoryI;
import com.starcut.auth.common.db.UserI;
import com.starcut.auth.email.service.AWSEmailService;
import com.starcut.auth.email.service.EmailServiceI;
import com.starcut.auth.email.service.SMTPEmailService;

public class EmailAuthConfiguration {

	@Value("${starcut.auth.email.service.type}")
	private String serviceType;

	@Value("${starcut.auth.email.from}")
	private String fromEmail;

	@Value("${starcut.auth.email.aws.accessKey}")
	private String awsAccessKey;

	@Value("${starcut.auth.email.aws.secretKey}")
	private String awsSecretKey;

	@Value("${starcut.auth.email.aws.region}")
	private String region;

	@Value("${starcut.auth.email.smtp.server}")
	private String smtpServer;

	@Value("${starcut.auth.email.smtp.port}")
	private Integer smtpPort;

	@Value("${starcut.auth.email.jwtSecret}")
	private String jwtSecret;

	private final Logger LOGGER = LoggerFactory.getLogger(EmailAuthConfiguration.class);

	@Bean
	public EmailServiceI getEmailService() {
		String type = serviceType.toLowerCase();
		if (type.equals("aws")) {
			return new AWSEmailService(fromEmail, awsAccessKey, awsSecretKey, region);
		} else if (type.equals("smtp")) {
			return new SMTPEmailService(fromEmail, smtpServer, smtpPort);
		} else {
			LOGGER.error("Invalid email service type: '" + type
					+ "'. Check your configuration file for starcut.auth.email.service.type, should be 'AWS' or 'SMTP'");
			return null;
		}
	}

	@Bean
	@ConditionalOnMissingBean
	public EmailUserRepositoryI<UserI> getUserRepository() {
		return new DummyUserRepository();
	}

	@Bean
	@ConditionalOnMissingBean
	public RoleProviderI<UserI> getRoleProvider() {
		return new DummyRoleProvider();
	}

	public String getJwtSecret() {
		return this.jwtSecret;
	}
}
