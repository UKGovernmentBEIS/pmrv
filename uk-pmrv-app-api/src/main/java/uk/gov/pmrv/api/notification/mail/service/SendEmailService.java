package uk.gov.pmrv.api.notification.mail.service;

import uk.gov.pmrv.api.notification.mail.domain.Email;

/**
 * Service for sending mails.
 */
public interface SendEmailService {

    /**
     * Sends an email with the parameters provided in the Mail object.
     * @param email {@link Email}
     */
    void sendMail(Email email);
}
