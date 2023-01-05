package com.pcc.utils;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.pcc.app.Application;

public class FtpConnection {

	public void con() {

		String server = Application.configProps.getProperty("pcc.ftp.host", ""); // "FTP.dssinetwork.com";
		int port = 21;
		String user = Application.configProps.getProperty("pcc.ftp.username", "");// "HDG";
		String pass = Application.configProps.getProperty("pcc.ftp.password", "");// "Ay48pMM";
		String remotePath = Application.configProps.getProperty("pcc.ftp.remotepath", "/In/Test/");// "Ay48pMM";
		String localPath = Application.configProps.getProperty("pcc.ftp.localpath", "C:/PCC_DOWNLOADED_FILES/");

		FTPClient ftpClient = new FTPClient() {
		};
		try {

			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

			String[] remoteFiles = ftpClient.listNames(remotePath);

			if (remoteFiles.length == 0) {
				System.out.println("No file to process at path " + remotePath);
			}

			for (String remoteFile : remoteFiles) {

				InputStream readingStream = ftpClient.retrieveFileStream(remotePath + remoteFile);
				OutputStream writingStream = new BufferedOutputStream(new FileOutputStream(localPath + remoteFile));
				byte[] bytesArray = new byte[4096];
				int bytesRead = -1;
				while ((bytesRead = readingStream.read(bytesArray)) != -1) {
					writingStream.write(bytesArray, 0, bytesRead);
				}

				boolean success = ftpClient.completePendingCommand();
				if (success) {
					System.out.println(remoteFile + " has been downloaded successfully.");
				}
				
				writingStream.close();
				readingStream.close();
				ftpClient.deleteFile(remotePath + remoteFile);
			}

			/*
			 * LocalDateTime timestamp = LocalDateTime.now();
			 * 
			 * DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd-MM-yyy HH-mm-ss");
			 * String fg = timestamp.format(DTF);
			 * 
			 * String remoteFile2 = "/In/Test/HDG_invout_HDG-2_20201130_TEST3.csv"; File
			 * downloadFile2 = new File("C:/FTP File/HDG_invout_HDG-2_20201130_TEST3." + fg
			 * + ".csv"); OutputStream outputStream2 = new BufferedOutputStream(new
			 * FileOutputStream(downloadFile2)); InputStream inputStream =
			 * ftpClient.retrieveFileStream(remoteFile2); byte[] bytesArray = new
			 * byte[4096]; int bytesRead = -1; while ((bytesRead =
			 * inputStream.read(bytesArray)) != -1) { outputStream2.write(bytesArray, 0,
			 * bytesRead); }
			 * 
			 * boolean success = ftpClient.completePendingCommand(); if (success) {
			 * System.out.println("File #1 has been downloaded successfully."); }
			 * outputStream2.close(); inputStream.close();
			 */

		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

}
