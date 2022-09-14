package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningCreateValidatorTest {

    @InjectMocks
    private InstallationAccountOpeningCreateValidator validator;
    
    @Test
    void validateAction_valid() {
        Long accountId = null;
        
        RequestCreateValidationResult result = validator.validateAction(accountId);
        
        assertThat(result.isValid()).isTrue();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        assertThat(result.getReportedAccountStatus()).isNull();
    }
    
    @Test
    void validateAction_invalid() {
        Long accountId = 1L;
        
        RequestCreateValidationResult result = validator.validateAction(accountId);
        
        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).isEmpty();
        assertThat(result.getReportedAccountStatus()).isNull();
    }
}
