package uk.gov.pmrv.api.workflow.request.flow.rde.validation;

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
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitRdeValidatorServiceTest {

    @InjectMocks
    private SubmitRdeValidatorService service;

    @Mock
    private WorkflowUsersValidator workflowUsersValidator;

    @Test
    void validate() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .dueDate(LocalDate.now())
                .build();
        final RdePayload rdePayload = RdePayload.builder().extensionDate(LocalDate.now().plusDays(5))
                .deadline(LocalDate.now().plusDays(2))
                .operators(Set.of("operator")).signatory("signatory").build();

        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator"), pmrvUser)).thenReturn(true);
        when(workflowUsersValidator.isSignatoryValid(requestTask, "signatory")).thenReturn(true);

        // Invoke
        service.validate(requestTask, rdePayload, pmrvUser);

        // Verify
        verify(workflowUsersValidator, times(1)).areOperatorsValid(1L, Set.of("operator"), pmrvUser);
        verify(workflowUsersValidator, times(1)).isSignatoryValid(requestTask, "signatory");
    }

    @Test
    void validate_whenIncompatibleType_thenThrowException() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .dueDate(LocalDate.now())
                .build();
        final RdePayload rdePayload = RdePayload.builder().extensionDate(LocalDate.now().plusDays(5))
                .deadline(LocalDate.now().plusDays(2))
                .operators(Set.of("operator")).signatory("signatory").build();

        when(workflowUsersValidator.areOperatorsValid(1L, Set.of("operator"), pmrvUser)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.validate(requestTask, rdePayload, pmrvUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(workflowUsersValidator, times(1)).areOperatorsValid(1L, Set.of("operator"), pmrvUser);
        verify(workflowUsersValidator, never()).isSignatoryValid(any(), anyString());
    }

    @Test
    void validate_whenExtensionDateBeforeDueDate_thenThrowException() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .dueDate(LocalDate.now().plusDays(5))
                .build();
        final RdePayload rdePayload = RdePayload.builder().extensionDate(LocalDate.now().plusDays(4))
                .deadline(LocalDate.now().plusDays(3))
                .operators(Set.of("operator")).signatory("signatory").build();

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.validate(requestTask, rdePayload, pmrvUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(workflowUsersValidator, never()).areOperatorsValid(anyLong(), anySet(), any());
        verify(workflowUsersValidator, never()).isSignatoryValid(any(), anyString());
    }

    @Test
    void validate_whenDeadlineAfterExtensionDate_thenThrowException() {
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().accountId(1L).build())
                .dueDate(LocalDate.now())
                .build();
        final RdePayload rdePayload = RdePayload.builder().extensionDate(LocalDate.now().plusDays(1))
                .deadline(LocalDate.now().plusDays(2))
                .operators(Set.of("operator")).signatory("signatory").build();

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.validate(requestTask, rdePayload, pmrvUser));

        // Verify
        assertEquals(ErrorCode.FORM_VALIDATION, businessException.getErrorCode());
        verify(workflowUsersValidator, never()).areOperatorsValid(anyLong(), anySet(), any());
        verify(workflowUsersValidator, never()).isSignatoryValid(any(), anyString());
    }
}
