package uk.gov.pmrv.api.migration.ftp;

public class FtpException extends Exception {

    private static final long serialVersionUID = 1L;

    public FtpException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
