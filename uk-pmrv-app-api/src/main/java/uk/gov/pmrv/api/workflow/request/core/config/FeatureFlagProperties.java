package uk.gov.pmrv.api.workflow.request.core.config;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ConfigurationProperties(prefix = "feature-flag")
@Getter
@Setter
public class FeatureFlagProperties {

    private Set<RequestType> disabledWorkflows = new HashSet<>();
}
