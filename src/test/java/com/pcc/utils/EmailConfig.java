package com.pcc.utils;

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

public class EmailConfig {

	public void emailsent() throws IOException {
		
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
				// return new javax.mail.PasswordAuthentication("pratik.201952@gmail.com","inluqpdjgjlmcnko");
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

			Multipart emailContent = new MimeMultipart();
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(Application.configProps.getProperty("pcc.mail.body", ""));

			MimeBodyPart pdfattachment = new MimeBodyPart();
			String localPath = Application.configProps.getProperty("pcc.ftp.localpath", "C://PCC//DOWNLOADED_FILES//");
			pdfattachment.attachFile(localPath+"HDG_invout_HDG-2_20201130_TEST3.csv");

			emailContent.addBodyPart(textBodyPart);
			emailContent.addBodyPart(pdfattachment);
			message.setContent(emailContent);

			System.out.println("Email sending process started");
			// Send message
			Transport.send(message);
			System.out.println("Email sending completed");
		} catch (MessagingException e) {
			System.out.println("Error in email sending");
			throw new RuntimeException(e);
		}
	}
}