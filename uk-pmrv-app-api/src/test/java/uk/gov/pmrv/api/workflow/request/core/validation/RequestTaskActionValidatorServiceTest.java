package uk.gov.pmrv.api.workflow.request.core.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

@ExtendWith(MockitoExtension.class)
class RequestTaskActionValidatorServiceTest {

    @InjectMocks
    private RequestTaskActionValidatorService requestTaskActionValidatorService;

    @Spy
    private ArrayList<RequestTaskActionValidator> requestTaskActionValidators;

    @Mock
    private PaymentTaskExistenceRequestTaskActionValidator paymentTaskExistenceRequestTaskActionValidator;

    @BeforeEach
    void setUp() {
        requestTaskActionValidators.add(paymentTaskExistenceRequestTaskActionValidator);
    }

    @Test
    void validate() {
        RequestTask requestTask = RequestTask.builder().build();
        RequestTaskActionType taskActionType = RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW;
        RequestTaskActionValidationResult validationResult = RequestTaskActionValidationResult.builder().valid(true).build();

        when(paymentTaskExistenceRequestTaskActionValidator.getTypes()).thenReturn(Set.of(taskActionType));
        when(paymentTaskExistenceRequestTaskActionValidator.validate(requestTask)).thenReturn(validationResult);

        requestTaskActionValidatorService.validate(requestTask, taskActionType);
    }

    @Test
    void validate_invalid() {
        RequestTask requestTask = RequestTask.builder().build();
        RequestTaskActionType taskActionType = RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW;
        RequestTaskActionValidationResult validationResult = RequestTaskActionValidationResult.builder()
            .valid(false)
            .errorMessage(RequestTaskActionValidationResult.ErrorMessage.PAYMENT_IN_PROGRESS)
            .build();

        when(paymentTaskExistenceRequestTaskActionValidator.getTypes()).thenReturn(Set.of(taskActionType));
        when(paymentTaskExistenceRequestTaskActionValidator.validate(requestTask)).thenReturn(validationResult);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> requestTaskActionValidatorService.validate(requestTask, taskActionType));


        assertEquals(ErrorCode.REQUEST_TASK_ACTION_CANNOT_PROCEED, businessException.getErrorCode());

        Object[] errors = businessException.getData();
        assertThat(errors).containsOnly(RequestTaskActionValidationResult.ErrorMessage.PAYMENT_IN_PROGRESS);
    }

    @Test
    void validate_no_validator_matched() {
        RequestTask requestTask = RequestTask.builder().build();
        RequestTaskActionType taskActionType = RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW;

        when(paymentTaskExistenceRequestTaskActionValidator.getTypes()).thenReturn(Set.of(taskActionType));

        requestTaskActionValidatorService.validate(requestTask, RequestTaskActionType.PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION);

        verify(paymentTaskExistenceRequestTaskActionValidator, never()).validate(any());
    }
}