package uk.gov.pmrv.api.migration.ftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@Component
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Log4j2
class FtpClientImpl implements FtpClient {
    private final FtpProperties ftpProperties;

    private JSch jsch;
    private Session sshSession;
    private ChannelSftp sftpChannel;
    
    @Override
    public byte[] fetchFile(String file) throws FtpException {
        try (FtpClientImpl client = this; 
                    ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            client.connect();
            sftpChannel.get(file, out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new FtpException("Fetching file from FTP server failed. Reason: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] fetchFileBatch(String file) throws FtpException {
        try {
            if(jsch == null) {
                connect();
            }
                
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                sftpChannel.get(file, out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new FtpException("Fetching file from FTP server failed. Reason: " + e.getMessage(), e);
        }
        
    }
    
    @Override
    public void healthCheck() {
        try(FtpClientImpl client = this) {
            client.connect();
            log.info("FTP server is healthy");
        } catch (JSchException | IOException e) {
            log.error("Cannot connect to FTP server. Error: " + e.getMessage());
        }
    }
    
    private void connect() throws JSchException, IOException {
        close();
        
        jsch = new JSch();

        //add ppk key
        jsch.addIdentity(ftpProperties.getKeyPath().getURI().getPath());
        
        //get session
        sshSession = jsch.getSession(ftpProperties.getUsername(), ftpProperties.getUrl(),  ftpProperties.getPort());
        
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);

        //connect to session
        sshSession.connect();
        
        //open and connect to sftp channel
        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        sftpChannel = (ChannelSftp) channel;
    }

    @Override
    public void close() {
        if(sftpChannel != null) {
            sftpChannel.exit();
        }
        
        if(sshSession != null) {
            sshSession.disconnect();
        }
        
        jsch = null;
        sshSession = null;
        sftpChannel = null;
    }

}
