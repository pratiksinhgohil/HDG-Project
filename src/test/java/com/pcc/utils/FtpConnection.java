package com.pcc.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.pcc.app.Application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info("Connecting to FTP server {}",server);
		try {
			ftpClient.connect(server, port);
			ftpClient.login(user, pass);
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			return true;

		} catch (IOException ex) {
			log.info("Error in FTP connection : " + ex.getMessage());
			ex.printStackTrace();
			return false;
		}

	}

	public int downloadFiles() {
		log.info("Downloading files");
		try {
			String[] remoteFiles = ftpClient.listNames(remotePath);

			if (remoteFiles != null && remoteFiles.length == 0) {
				log.info("No file to process at path " + remotePath);
				return 0;
			}
			int fileCounter = 0;
			for (String remoteFile : remoteFiles) {

				if (remoteFile.endsWith(".csv")) {
					String remoteFileToDelete = remotePath +"/"+ remoteFile;
					log.info("Remote file : " + remoteFileToDelete + " copying to "
							+ Application.CURRENT_HOUR_FOLDER + "//" + remoteFile);

					InputStream readingStream = ftpClient.retrieveFileStream(remoteFileToDelete);

					File file = new File(Application.CURRENT_HOUR_FOLDER + "//" + remoteFile);
					file.getParentFile().mkdirs();
					if (file.exists()) {
						file.delete();
						file.createNewFile();
					} else {
						file.createNewFile();
					}

					OutputStream writingStream = new BufferedOutputStream(
							new FileOutputStream(Application.CURRENT_HOUR_FOLDER + "//" + remoteFile));

					byte[] bytesArray = new byte[4096];
					int bytesRead = -1;
					while ((bytesRead = readingStream.read(bytesArray)) != -1) {
						writingStream.write(bytesArray, 0, bytesRead);
					}

					boolean success = ftpClient.completePendingCommand();

					if (success) {
						log.info(" {}// {} has been downloaded successfully.",Application.CURRENT_HOUR_FOLDER,remoteFile);
					}

					writingStream.close();
					readingStream.close();

					boolean deleteFile = true; 
//					boolean deleteFile = ftpClient.deleteFile(remoteFileToDelete);
					log.info("Remote file {} delete status {}",remoteFileToDelete,deleteFile);

					fileCounter++;
				}
			}

			return fileCounter; 

		} catch (IOException ex) {
			log.info("Error in FTP connection : " + ex.getMessage());
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
