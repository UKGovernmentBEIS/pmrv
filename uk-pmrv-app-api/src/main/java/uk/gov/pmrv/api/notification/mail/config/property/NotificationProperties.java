package uk.gov.pmrv.api.notification.mail.config.property;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "notification")
@Getter
@Setter
public class NotificationProperties {

    private Email email;

    @Getter
    @Setter
    public static class Email {
        @NotEmpty
        private String autoSender;

        @NotEmpty
        private String officialContact;
    }
}
