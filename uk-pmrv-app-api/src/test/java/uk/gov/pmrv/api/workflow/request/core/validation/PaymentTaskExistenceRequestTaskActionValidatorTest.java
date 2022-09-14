package uk.gov.pmrv.api.workflow.request.core.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

class PaymentTaskExistenceRequestTaskActionValidatorTest {

    private final PaymentTaskExistenceRequestTaskActionValidator validator = new PaymentTaskExistenceRequestTaskActionValidator();

    @Test
    void validate_invalid() {
        final RequestTask reviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();
        final RequestTask paymentTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_CONFIRM_PAYMENT)
            .build();
        final Request request = Request.builder().id("1").requestTasks(List.of(reviewTask, paymentTask)).build();
        reviewTask.setRequest(request);

        RequestTaskActionValidationResult result = validator.validate(reviewTask);

        assertFalse(result.isValid());
    }

    @Test
    void validate_valid() {

        final RequestTask reviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();
        final RequestTask peerReviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW)
            .build();
        final Request request = Request.builder().id("1").requestTasks(List.of(reviewTask, peerReviewTask)).build();
        reviewTask.setRequest(request);

        RequestTaskActionValidationResult result = validator.validate(reviewTask);

        assertTrue(result.isValid());
    }

    @Test
    void getTypes() {
        Set<RequestTaskActionType> requestTaskActionTypes = new HashSet<>();
        requestTaskActionTypes.addAll(RequestTaskActionType.getNotifyOperatorForDecisionTypes());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRequestPeerReviewTypes());
        requestTaskActionTypes.addAll(RequestTaskActionType.getRfiRdeSubmissionTypes());
        requestTaskActionTypes.add(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION);

        assertEquals(requestTaskActionTypes, validator.getTypes());
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(RequestTaskType.getRegulatorPaymentTypes(), validator.getConflictingRequestTaskTypes());
    }

}