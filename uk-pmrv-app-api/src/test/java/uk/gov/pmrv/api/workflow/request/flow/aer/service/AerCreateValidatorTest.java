package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Year;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@ExtendWith(MockitoExtension.class)
class AerCreateValidatorTest {

    @InjectMocks
    private AerCreateValidator validator;

    @Mock
    private AerCreateValidatorService aerValidatorService;

    @Test
    void validateAction() {
        final Long accountId = 1L;
        final Year year = Year.now();

        when(aerValidatorService.validate(accountId, year))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        // Invoke
        final RequestCreateValidationResult result = validator.validateAction(accountId);

        // Verify
        assertThat(result).isEqualTo(RequestCreateValidationResult.builder().valid(true).build());
        verify(aerValidatorService, times(1)).validate(accountId, year);
    }

    @Test
    void type() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.AER);
    }
}
