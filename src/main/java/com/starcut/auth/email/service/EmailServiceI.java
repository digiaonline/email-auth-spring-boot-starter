package com.starcut.auth.email.service;

public interface EmailServiceI {

	/**
	 * Send an email
	 * @param to The email address to send the email to
	 * @param subject The subject of the email
	 * @param textBody The content in plain text
	 * @param htmlBody The content in html
	 * @return True if the email is sent (or the request is done)
	 */
	public boolean sendEmail(String to, String subject, String textBody, String htmlBody);
}
