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
	private FTPClient ftpClient = new FTPClient();
	private final String server = Application.configProps.getProperty("pcc.ftp.host", ""); // "FTP.dssinetwork.com";
	private final int port = 21;
	private final String user = Application.configProps.getProperty("pcc.ftp.username", "");// "HDG";
	private final String pass = Application.configProps.getProperty("pcc.ftp.password", "");// "Ay48pMM";
	private final String remotePath = Application.configProps.getProperty("pcc.ftp.remotepath", "//In//Test//");// "Ay48pMM";

	public FtpConnection() {

	}

	public boolean connect() {
		System.out.println("Connecting to FTP");
		try {
			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			return true;

		} catch (IOException ex) {
			System.out.println("Error in FTP connection : " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}

	}

	public int downloadFiles() {
		System.out.println("Downloading files");
		try {
			String[] remoteFiles = ftpClient.listNames(remotePath);

			if (remoteFiles.length == 0) {
				System.out.println("No file to process at path " + remotePath);
				return 0;
			}
			int fileCounter = 0;
			for (String remoteFile : remoteFiles) {

				if (remoteFile.endsWith(".csv")) {
					System.out.println("Remote file : " + remotePath + remoteFile + " copying to "
							+ Application.CURRENT_HOUR_FOLDER + "//" + remoteFile);

					InputStream readingStream = ftpClient.retrieveFileStream(remotePath + remoteFile);
					OutputStream writingStream = new BufferedOutputStream(
							new FileOutputStream(Application.CURRENT_HOUR_FOLDER + "//" + remoteFile));

					byte[] bytesArray = new byte[4096];
					int bytesRead = -1;
					while ((bytesRead = readingStream.read(bytesArray)) != -1) {
						writingStream.write(bytesArray, 0, bytesRead);
					}

					boolean success = ftpClient.completePendingCommand();

					if (success) {
						System.out.println(Application.CURRENT_HOUR_FOLDER + "//"  + remoteFile + " has been downloaded successfully.");
					}

					writingStream.close();
					readingStream.close();

					ftpClient.deleteFile(remotePath + remoteFile);

					fileCounter++;
				}
			}

			return fileCounter;
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
			System.out.println("Error in FTP connection : " + ex.getMessage());
			ex.printStackTrace();
			return 0;
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
