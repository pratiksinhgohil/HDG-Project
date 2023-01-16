package com.pcc.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EmailConfig {

	public static void sendEmail(boolean invalidFiles, String filePath) throws IOException {

		Properties prop = new Properties();
		prop.put("mail.smtp.host", Application.configProps.getProperty("pcc.mail.host", ""));// "smtp.gmail.com"
		// prop.put("mail.smtp.socketFactory.class","java.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.ssl.enable", Application.configProps.getProperty("pcc.mail.ssl.enable", "true"));// "true"
		prop.put("mail.smtp.auth", Application.configProps.getProperty("pcc.mail.auth", "true"));
		// prop.put("mail.smtp.port", "465");

		String senderEmail = Application.configProps.getProperty("pcc.mail.sender.email");
		String[] receiverEmails = Application.configProps.getProperty("pcc.mail.receivers.emailids").split(",");
		String senderPassword = Application.configProps.getProperty("pcc.mail.sender.password");

		Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				// return new
				// javax.mail.PasswordAuthentication("pratik.201952@gmail.com","inluqpdjgjlmcnko");
				return new javax.mail.PasswordAuthentication(senderEmail, senderPassword);
			}
		});

		try {

			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmail));
			List<InternetAddress> emailsList = new ArrayList<>();
			for (String receivers : receiverEmails) {
				emailsList.add(new InternetAddress(receivers));
			}

			// message.addRecipient(Message.RecipientType.TO,new
			// InternetAddress("pratiksinh.gohil@tntra.io", "pratisinh15@gmail.com"));
			message.addRecipients(Message.RecipientType.TO, emailsList.toArray(new InternetAddress[0]));
			message.setSubject(Application.configProps.getProperty("pcc.mail.subject.prefix", "") + "");
			message.setText(Application.configProps.getProperty("pcc.mail.text", ""));

			Multipart email = new MimeMultipart();
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(Application.configProps.getProperty("pcc.mail.body", ""));

			MimeBodyPart body = new MimeBodyPart();
			// TODO Changes file to exact path

			if (invalidFiles) {
				File folder = new File(Application.CURRENT_HOUR_FOLDER_IN_VALID_FILES);
				File[] listOfFiles = folder.listFiles();
				for (File file : listOfFiles) {
					body.attachFile(file.getCanonicalPath());
				}
			} else {
				body.attachFile(filePath);
			}

			email.addBodyPart(textBodyPart);
			email.addBodyPart(body);
			message.setContent(email);

			log.info("Email sending process started");
			// Send message
			Transport.send(message);
			log.info("Email sending completed");
		} catch (MessagingException e) {
			log.info("Error in email sending");
			throw new RuntimeException(e);
		}
	}
}