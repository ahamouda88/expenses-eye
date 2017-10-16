package com.expensesEye.service;

import java.io.IOException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.expensesEye.config.SpringBootConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringBootConfig.class, EmailService.class })
public class EmailServiceTest {

	@Autowired
	private EmailService emailService;

	@Before
	public void setUp() {
		Mailbox.clearAll();
	}

	@Test
	public void testSendEmail() throws MessagingException, IOException {
		String subject = "Subject";
		String body = "Message Body";
		boolean sent = emailService.sendEmail("source@hotmail.com", "dest@hotmail.com", subject, body);
		Assert.assertTrue(sent);

		List<Message> inbox = Mailbox.get("dest@hotmail.com");

		Assert.assertEquals(inbox.size(), 1);
		Assert.assertEquals(inbox.get(0).getSubject(), subject);
		Assert.assertEquals(inbox.get(0).getContent(), body);
	}

	@Test
	public void testInvalidEmail() throws MessagingException {
		String subject = "Subject";
		String body = "Message Body";
		boolean sent = emailService.sendEmail("invalidEmail", null, subject, body);
		Assert.assertFalse(sent);

		List<Message> inbox = Mailbox.get("dest@hotmail.com");

		Assert.assertEquals(inbox.size(), 0);
	}
}
