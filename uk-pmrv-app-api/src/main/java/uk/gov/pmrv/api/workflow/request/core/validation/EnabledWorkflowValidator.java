package uk.gov.pmrv.api.workflow.request.core.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.config.FeatureFlagProperties;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Service
@RequiredArgsConstructor
public class EnabledWorkflowValidator {

    private final FeatureFlagProperties featureFlagProperties;

    public boolean isWorkflowEnabled(RequestType requestType) {
        return !featureFlagProperties.getDisabledWorkflows().contains(requestType);
    }
}
