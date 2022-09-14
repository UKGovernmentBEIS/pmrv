package uk.gov.pmrv.api.workflow.payment.config.property;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "govuk-pay")
@Data
public class GovukPayProperties {

    @NotBlank
    private String serviceUrl;

    @NotEmpty
    private Map<String, String> apiKeys = new HashMap<>();
}
