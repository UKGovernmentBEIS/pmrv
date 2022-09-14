package uk.gov.pmrv.api.user.core.config;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    private Claim claim;

    @Getter
    @Setter
    public static class Claim {

        @NotEmpty
        private String audience;

        private long userInvitationExpIntervalMinutes = 4320L;

        private long change2faExpIntervalMinutes = 5L;
        
        private long getFileAttachmentExpIntervalMinutes = 1L;
    }


}
