package com.expensesEye.service;

/**
 * An interface that contains the logic for doing operations on emails
 */
public interface EmailService {

	/**
	 * A method that sends an email to a given email address
	 * 
	 * @param fromEmail
	 *            the email address that will send the message
	 * @param toEmail
	 *            the email address to send the message to
	 * @param subject
	 *            the subject of the message
	 * @param body
	 *            the body of the message
	 * @return true if email was send successfully, otherwise it will return false
	 */
	public boolean sendEmail(String fromEmail, String toEmail, String subject, String body);

	/**
	 * A method that sends an email to a given email address
	 * 
	 * @param fromEmail
	 *            the email address that will send the message
	 * @param toEmail
	 *            the email address to send the message to
	 * @param subject
	 *            the subject of the message
	 * @param body
	 *            the body of the message
	 * @param contentType
	 *            the type of the content
	 * @return true if email was send successfully, otherwise it will return false
	 */
	public boolean sendEmail(String fromEmail, String toEmail, String subject, String body, String contentType);
}
