package com.starcut.auth.email.service;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.starcut.auth.email.link.LoginLinkProvider;

@Service
public class EmailLoginService<UserI> {

	@Autowired
	LoginLinkProvider loginLinkProvider;

	@Autowired
	EmailServiceI emailService;

	private final Logger LOGGER = LoggerFactory.getLogger(EmailLoginService.class);

	public boolean sendEmailLogin(String to, String subject, String textTemplate, String htmlTemplate) {
		return sendEmailLogin(to, subject, textTemplate, htmlTemplate, null);
	}

	public boolean sendEmailLogin(String to, String subject, String textTemplate, String htmlTemplate,
			String redirect) {
		String loginLink;
		try {
			if (redirect != null) {
				loginLink = loginLinkProvider.produceLoginLink(to, redirect);
			} else {
				loginLink = loginLinkProvider.produceLoginLink(to);
			}
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Failed to generate the login link for '" + to + "'.");
			return false;
		}
		String htmlBody = String.format(htmlTemplate, loginLink, loginLink);
		String textBody = String.format(textTemplate, loginLink);
		return emailService.sendEmail(to, subject, textBody, htmlBody);
	}

}
