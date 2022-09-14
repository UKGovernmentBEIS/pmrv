package uk.gov.pmrv.api.feedback.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "user-feedback")
@Getter
@Setter
public class UserFeedbackConfig {

    private List<String> recipients = new ArrayList<>();

}
