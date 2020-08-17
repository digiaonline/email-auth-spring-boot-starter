package com.starcut.auth.email.service;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class AWSEmailService implements EmailServiceI {

	private AmazonSimpleEmailService amazonSimpleEmailService;

	private String fromEmail;

	private final static String CHARSET = StandardCharsets.UTF_8.name();
	private final Logger LOGGER = LoggerFactory.getLogger(AWSEmailService.class);

	public AWSEmailService(String fromEmail, String awsAccessKey, String awsSecretKey, String region) {
		this.fromEmail = fromEmail;

		this.amazonSimpleEmailService = 
		          AmazonSimpleEmailServiceClientBuilder.standard()
		            .withRegion(Regions.fromName(region)).build();
	}

	@Override
	public boolean sendEmail(String to, String subject, String textBody, String htmlBody) {
		try {
			sendEmailWithAWS(Arrays.asList(new InternetAddress(to)), subject, htmlBody, textBody);
		} catch (AddressException e) {
			LOGGER.error("Cannot send email to '" + to + "', invalid address.", e);
			return false;
		}
		return true;
	}

	@Async
	public void sendEmailWithAWS(List<InternetAddress> to, String subject, String htmlBody, String textBody) {
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(
						new Destination().withToAddresses(to.stream().map(a -> a.getAddress()).toArray(String[]::new)))
				.withMessage(new Message()
						.withBody(new Body().withHtml(new Content().withCharset(CHARSET).withData(htmlBody))
								.withText(new Content().withCharset(CHARSET).withData(textBody)))
						.withSubject(new Content().withCharset(CHARSET).withData(subject)))
				.withSource(fromEmail);
		amazonSimpleEmailService.sendEmail(request);
		LOGGER.info("Email sent!");
	}
}
