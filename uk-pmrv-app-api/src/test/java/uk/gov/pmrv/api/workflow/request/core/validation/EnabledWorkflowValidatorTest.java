package uk.gov.pmrv.api.workflow.request.core.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.config.FeatureFlagProperties;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

public class EnabledWorkflowValidatorTest {

    @Test
    public void isWorkflowAllowed() {
        FeatureFlagProperties featureFlagProperties = new FeatureFlagProperties();
        featureFlagProperties.setDisabledWorkflows(Set.of(RequestType.PERMIT_ISSUANCE));

        EnabledWorkflowValidator enabledWorkflowValidator = new EnabledWorkflowValidator(featureFlagProperties);

        boolean isAllowed = enabledWorkflowValidator.isWorkflowEnabled(RequestType.INSTALLATION_ACCOUNT_OPENING);

        assertTrue(isAllowed);

        isAllowed = enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_ISSUANCE);

        assertFalse(isAllowed);
    }

    @Test
    public void isWorkflowAllowed_when_all_workflows_enabled() {
        FeatureFlagProperties featureFlagProperties = new FeatureFlagProperties();

        EnabledWorkflowValidator enabledWorkflowValidator = new EnabledWorkflowValidator(featureFlagProperties);

        boolean isAllowed = enabledWorkflowValidator.isWorkflowEnabled(RequestType.INSTALLATION_ACCOUNT_OPENING);

        assertTrue(isAllowed);

        isAllowed = enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_ISSUANCE);

        assertTrue(isAllowed);
    }

}
