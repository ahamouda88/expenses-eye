package com.expensesEye.service.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.expensesEye.model.ErrorMessages;
import com.expensesEye.service.EmailService;

/**
 * Implementation of {@link EmailService}
 */
@Service
public class EmailServiceImpl implements EmailService {
	private static Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Override
	public boolean sendEmail(String fromEmail, String toEmail, String subject, String body) {
		return sendEmail(fromEmail, toEmail, subject, body, null);
	}

	@Override
	public boolean sendEmail(String fromEmail, String toEmail, String subject, String body, String contentType) {
		if (StringUtils.isEmpty(fromEmail)) {
			logger.error(String.format(ErrorMessages.INVALID_PARAM_EMAIL, fromEmail));
			return false;
		}
		if (StringUtils.isEmpty(toEmail)) {
			logger.error(String.format(ErrorMessages.INVALID_PARAM_EMAIL, toEmail));
			return false;
		}
		Properties props = new Properties();
		// Setup mail server
		props.setProperty("mail.smtp.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.port", "587");
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.ssl.trust", "*");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("expenseseye@gmail.com", "expenses_eye");
			}
		});

		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(fromEmail));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			msg.setSubject(subject);
			if (StringUtils.isEmpty(contentType)) {
				msg.setText(body);
			} else {
				msg.setContent(body, contentType);
			}
			Transport.send(msg);
			return true;
		} catch (Exception e) {
			logger.error(ErrorMessages.FAILED_SEND_EMAIL + ": " + e.getMessage());
		}
		return false;
	}

}
