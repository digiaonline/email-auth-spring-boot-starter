package com.starcut.auth.email.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SMTPEmailService implements EmailServiceI {

	private JavaMailSenderImpl mailSender;

	private String fromEmail;

	private final Logger LOGGER = LoggerFactory.getLogger(SMTPEmailService.class);

	public SMTPEmailService(String fromEmail, String smtpHost, Integer smtpPort) {
		this.mailSender = new JavaMailSenderImpl();
		this.mailSender.setHost(smtpHost);
		this.mailSender.setPort(smtpPort);
		this.fromEmail = fromEmail;
	}

	@Override
	public boolean sendEmail(String to, String subject, String textBody, String htmlBody) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		try {
			helper.setTo(to);
			helper.setFrom(fromEmail);
			helper.setText(htmlBody, true);
			helper.setText(textBody, false);
			helper.setSubject(subject);
			LOGGER.debug("Sent an email to '" + to + "' with:\n***\n " + textBody + "***\n to ");
			mailSender.send(message);
			return true;
		} catch (MessagingException e) {
			LOGGER.error("Failed to send an email to '" + to + "'.", e);
			return false;
		}
	}
}
