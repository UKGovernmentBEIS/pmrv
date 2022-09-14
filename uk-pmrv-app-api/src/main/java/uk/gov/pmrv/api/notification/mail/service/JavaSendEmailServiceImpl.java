package uk.gov.pmrv.api.notification.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.mail.domain.Email;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Log4j2
@Service
@RequiredArgsConstructor
public class JavaSendEmailServiceImpl implements SendEmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(Email email) {
        log.debug("Sending mail with subject {} to: {}", email::getSubject,
            () -> String.join(",", email.getSendTo()));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom(email.getSendFrom());
            messageHelper.setTo(email.getSendTo().toArray(String[]::new));
            messageHelper.setCc(email.getSendCc().toArray(String[]::new));
            messageHelper.setSubject(email.getSubject());
            messageHelper.setText(email.getText(), true);
            
            for(Map.Entry<String, byte[]> attachment : email.getAttachments().entrySet()) {
                messageHelper.addAttachment(attachment.getKey(), new ByteArrayResource(attachment.getValue()));    
            }

            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            log.error("Exception during sending email:", e);
        }
    }
}
