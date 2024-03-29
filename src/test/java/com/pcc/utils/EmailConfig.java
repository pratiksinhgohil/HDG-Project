package com.pcc.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailConfig {

	/**
	 * Gets the mail session.
	 *
	 * @return the mail session
	 */
	private static Session getMailSession() {
		Properties prop = new Properties();
		prop.put("mail.smtp.host", Application.APP_CONFIG.getConfigProps().getProperty("pcc.mail.host", ""));// "smtp.gmail.com"
		// prop.put("mail.smtp.socketFactory.class","java.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.ssl.enable",
				Application.APP_CONFIG.getConfigProps().getProperty("pcc.mail.ssl.enable", "true"));// "true"
		prop.put("mail.smtp.auth", Application.APP_CONFIG.getConfigProps().getProperty("pcc.mail.auth", "true"));
		// prop.put("mail.smtp.port", "465");

		return Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				return new javax.mail.PasswordAuthentication(Application.APP_CONFIG.getConfigProps().getProperty("pcc.mail.sender.email"),
						Application.APP_CONFIG.getConfigProps().getProperty("pcc.mail.sender.password"));
			}
		});
	}

	/**
	 * Send invalid files generated after validation
	 *
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 * @throws IOException        Signals that an I/O exception has occurred.
	 */
	public static void sendInvalidFiles() throws AddressException, MessagingException, IOException {
		Multipart email = new MimeMultipart();
		MimeBodyPart textBodyPart = new MimeBodyPart();

		StringBuilder sb = new StringBuilder("Dear User<br><br>");
		sb.append("<br>Attachment contains files for processing hour " + Application.APP_CONFIG.getCurrentHour());
		sb.append("<br> Download files and check message of ErrorMessage column </br>");
		sb.append(
				"<br><br> Note 1 : Please correct errors and remove column ErrorMessage and upload to FTP again to process in next hour </br>");
		sb.append("<br> Note 2 : Delete old files");
		// textBodyPart.setText(sb.toString());
		textBodyPart.setContent(sb.toString(), "text/html");
		email.addBodyPart(textBodyPart);
		try {
			File folder = new File(Application.APP_CONFIG.getCurrentHourFolderInValidFiles());//CURRENT_HOUR_FOLDER_IN_VALID_FILES
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles != null) {
				for (File file : listOfFiles) {
					MimeBodyPart attachment = new MimeBodyPart();
					attachment.attachFile(file.getCanonicalPath());
					email.addBodyPart(attachment);
				}
			}

		} catch (MessagingException | IOException e) {
			e.printStackTrace();
		}
		MimeMessage message = getMessage("Invalid files of processing hour " + Application.APP_CONFIG.getCurrentHour());
		message.setContent(email, "text/html");
		sendEmail(message);

	}

	/**
	 * Send line description in email. The line description details gathered while
	 * validation of files.
	 *
	 * @throws AddressException   the address exception
	 * @throws MessagingException the messaging exception
	 * @throws IOException        Signals that an I/O exception has occurred.
	 */
	public static void sendLineDescInEmail() throws AddressException, MessagingException, IOException {

		Multipart email = new MimeMultipart();
		MimeMessage message = getMessage(Application.APP_CONFIG.getCurrentHour() + "[ Line description details ]");
		MimeBodyPart textBodyPart = new MimeBodyPart();

		StringBuilder sb = new StringBuilder("Dear User");
		sb.append("<br><br>Following records shows line description received in csv files for processing hour:  "
				+ Application.APP_CONFIG.getCurrentHour() + ".");

		
		
		Application.APP_CONFIG.getLineDescriptionFiles().entrySet().forEach(entry -> {
			sb.append("<br><br>File : " + entry.getKey());
			sb.append("<br><br>" + EMAIL_TABLE_LINE_DESC);
			entry.getValue().forEach(val -> {
				sb.append(val);
			});
			sb.append("</table>");

		});

		textBodyPart.setContent(sb.toString(), "text/html");
		email.addBodyPart(textBodyPart);
		message.setContent(email, "text/html");
		sendEmail(message);

	}

	/**
	 * Send processing status.
	 *
	 * @param processingStatus Status message
	 * @param attachFiles
	 */
	public static void sendProcessingStatusEmail(String processingStatus, boolean attachFiles) {

		try {
			Multipart email = new MimeMultipart();

			MimeMessage message = getMessage(Application.APP_CONFIG.getCurrentHour() + ": File processing status");

			MimeBodyPart textBodyPart = new MimeBodyPart();

			StringBuilder sb = new StringBuilder("Dear User");
			sb.append("<br><br>Processing status message  :" + processingStatus);

			if (!Application.APP_CONFIG.getUploadProcessingStatus().isEmpty()) {
				sb.append("<br><br>Details of each file is as below  :<br><br>");
				sb.append(EMAIL_TABLE_PROCESSING_STATUS);
				Application.APP_CONFIG.getUploadProcessingStatus().entrySet().forEach(entry -> {
					sb.append("<tr><td>" + entry.getKey() + "</td><td>" + entry.getValue() + "</td></tr>");
				});
				sb.append("</table>");
				sb.append(
						"<br><br>If email contains pdf file in attachment then read each pdf file and correct csv file from attachment. After correction please upload csv file to FTP again.");

			}

			email.addBodyPart(textBodyPart);
			log.info("Attaching files from folder " + Application.APP_CONFIG.getCurrentHourFolder() + " with flag " + attachFiles);
			File folder = new File(Application.APP_CONFIG.getCurrentHourFolder());
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles != null && attachFiles) {
				for (File file : listOfFiles) {
					String fileName = file.getName();
					log.info("Attaching file(before isFileCcheck) " + fileName);
					try {
						if (file.isFile()) {
							try {
								MimeBodyPart csvFile = new MimeBodyPart();
								csvFile.attachFile(file.getCanonicalPath());
								email.addBodyPart(csvFile);
							} catch (MessagingException | IOException e) {
								e.printStackTrace();
							}

						} else if (file.isDirectory()) {
							log.info(fileName + " is not file");
						}
					} catch (Exception e) {
						log.info("Error while reading file " + fileName);
					}
				}
			}

			if (!Application.APP_CONFIG.getExceptionReports().isEmpty()) {
				for (String pdfPath : Application.APP_CONFIG.getExceptionReports()) {
					File fileObj = new File(pdfPath);
					if (fileObj.exists()) {
						try {
							MimeBodyPart pdfFileBody = new MimeBodyPart();
							pdfFileBody.attachFile(pdfPath);
							email.addBodyPart(pdfFileBody);

						} catch (Exception e) {
							log.info("Error while reading attaching pdf " + pdfPath + ">>." + e.getMessage());
						}
					} else {
						sb.append("<br><br>Exception report missing(Check browser setting)." + pdfPath);
					}
				}
			}
			textBodyPart.setContent(sb.toString(), "text/html");
			message.setContent(email, "text/html");
			sendEmail(message);

		} catch (Exception e) {
			log.info("Error while sending email final processing status " + e.getMessage());
		}

	}

	/**
	 * Generates MimeMessage with subject
	 *
	 * @param subject Provide subject line to add in message
	 * @return the message
	 */
	private static MimeMessage getMessage(String subject) {

		MimeMessage message = new MimeMessage(getMailSession());
		try {
			message.setFrom(Application.APP_CONFIG.getConfigProps().getProperty("pcc.mail.sender.email"));
			message.addRecipients(Message.RecipientType.TO, Application.APP_CONFIG.getEmailReceiver()
					.toArray(new InternetAddress[Application.APP_CONFIG.getEmailReceiver().size()]));
			message.setHeader("Content-Type", "text/html");
			message.setContent(message, "text/html");
			message.setSubject(subject);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * Send email.
	 *
	 * @param message the message
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void sendEmail(MimeMessage message) throws IOException {

		try {
			Transport.send(message);
			log.info("Email sending completed");
		} catch (MessagingException e) {
			e.printStackTrace();
			log.info("Error in email sending"+e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private static final String EMAIL_TABLE_LINE_DESC = "<table border='1' style='border-collapse:collapse;border:1px solid;' cellpadding='3'><thead><tr><th>VenCode</th><th>Invoice number</th><th>Line description</th></tr></thead>";

	private static final String EMAIL_TABLE_PROCESSING_STATUS = "<table border='1' style='border-collapse:collapse;border:1px solid;' cellpadding='3'><thead><tr><th>File name</th><th>Processing status</th></tr></thead>";

}
