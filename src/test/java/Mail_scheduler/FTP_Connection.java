package Mail_scheduler;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
public class FTP_Connection {

	public  void con(){
		// TODO Auto-generated method stub
		String server = "FTP.dssinetwork.com";
        int port = 21;
        String user = "HDG";
        String pass = "Ay48pMM";
 
        FTPClient ftpClient = new FTPClient() {
		};
        try {

            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            LocalDateTime timestamp =LocalDateTime.now();
            DateTimeFormatter DTF= DateTimeFormatter.ofPattern("dd-MM-yyy HH-mm-ss");
            String fg=timestamp.format(DTF);
            
           
            String remoteFile2 = "/In/Test/HDG_invout_HDG-2_20201130_TEST3.csv";
            File downloadFile2 = new File("C:/FTP File/HDG_invout_HDG-2_20201130_TEST3."+fg+".csv");
            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            byte[] bytesArray = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                outputStream2.write(bytesArray, 0, bytesRead);
            }
 
            boolean success = ftpClient.completePendingCommand();
            if (success) {
                System.out.println("File #1 has been downloaded successfully.");
            }
            outputStream2.close();
            inputStream.close();
 
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
