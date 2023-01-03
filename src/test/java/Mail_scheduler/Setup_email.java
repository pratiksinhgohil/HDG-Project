package Mail_scheduler;

import java.io.IOException;
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



public class Setup_email {

	public void emailsent() throws IOException{
		// TODO Auto-generated method stub\\
		Properties prop =new Properties();
		
		
		  prop.put("mail.smtp.host", "smtp.gmail.com");    
		
		
		//prop.put("mail.smtp.socketFactory.class","java.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.auth", "true");
		
		//prop.put("mail.smtp.port", "465");
		
		Session session = Session.getDefaultInstance (prop , new javax.mail.Authenticator(){

			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {

				return new javax.mail.PasswordAuthentication("pratik.201952@gmail.com","inluqpdjgjlmcnko");

			}

		});
				
		try {
			
			 MimeMessage message = new MimeMessage(session);

	            // Set From: header field of the header.
	            message.setFrom(new InternetAddress("pratik.201952@gmail.com"));

	            // Set To: header field of the header.
	            message.addRecipient(Message.RecipientType.TO, new InternetAddress("pratiksinh.gohil@tntra.io","pratisinh15@gmail.com"));

	            // Set Subject: header field
	            message.setSubject("This is the Subject Line!");

	            // Now set the actual message
	            message.setText("This is actual message");
	            
	            Multipart emailContent = new MimeMultipart();
				MimeBodyPart textBodyPart = new MimeBodyPart();
				textBodyPart.setText("my content");
				
				MimeBodyPart pdfattachment = new MimeBodyPart();
				pdfattachment.attachFile("C:/FTP File/HDG_invout_HDG-2_20201130_TEST3.27-12-2022 12-39-56.csv");
				
				emailContent.addBodyPart(textBodyPart);
				emailContent.addBodyPart(pdfattachment);
				message.setContent(emailContent);

	            System.out.println("sending...");
	            // Send message
	            Transport.send(message);
	            System.out.println("Sent message successfully....");
		} catch (MessagingException e) {

			throw new RuntimeException(e);

		}

	}


}