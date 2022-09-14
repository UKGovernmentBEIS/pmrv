package uk.gov.pmrv.api.workflow.request.flow.rfi.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.WorkflowUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmitPayload;

@ExtendWith(MockitoExtension.class)
class SubmitRfiValidatorServiceTest {

    @InjectMocks
    private SubmitRfiValidatorService service;

    @Mock
    private WorkflowUsersValidator workflowUsersValidator;

    @Test
    void validate() {

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().accountId(1L).build())
            .build();
        final RfiSubmitPayload
            rfiSubmitPayload = RfiSubmitPayload.builder().operators(Set.of("operator")).signatory("signatory").build();

        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator"), pmrvUser)).thenReturn(true);
        when(workflowUsersValidator.isSignatoryValid(requestTask, "signatory")).thenReturn(true);

        service.validate(requestTask, rfiSubmitPayload, pmrvUser);

        verify(workflowUsersValidator, times(1)).areOperatorsValid(1L, Set.of("operator"), pmrvUser);
        verify(workflowUsersValidator, times(1)).isSignatoryValid(requestTask, "signatory");
    }

    @Test
    void validate_whenIncompatibleType_thenThrowException() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .build();
        final RfiSubmitPayload
            rfiSubmitPayload = RfiSubmitPayload.builder().operators(Set.of("operator")).signatory("signatory").build();

        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator"), pmrvUser)).thenReturn(false);

        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.validate(requestTask, rfiSubmitPayload, pmrvUser));

        // Assert
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());

        verify(workflowUsersValidator, times(1)).areOperatorsValid(1L, Set.of("operator"), pmrvUser);
        verify(workflowUsersValidator, never()).isSignatoryValid(any(), anyString());
    }
}
