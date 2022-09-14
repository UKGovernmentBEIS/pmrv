package uk.gov.pmrv.api.workflow.request.core.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

class RfiRdeTaskExistenceRequestTaskActionValidatorTest {

    private final RfiRdeTaskExistenceRequestTaskActionValidator validator = new RfiRdeTaskExistenceRequestTaskActionValidator();

    @Test
    void validate_whenIncompatibleType() {

        final RequestTask reviewTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .build();
        final RequestTask waitRfIResponseTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE)
            .build();
        final Request request = Request.builder().id("1").requestTasks(List.of(reviewTask, waitRfIResponseTask)).build();
        reviewTask.setRequest(request);

        RequestTaskActionValidationResult result = validator.validate(reviewTask);

        assertFalse(result.isValid());
    }

    @Test
    void validate_whenCompatibleTypes() {

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
        assertThat(validator.getTypes()).isEqualTo(Set.of(
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RDE_SUBMIT)
        );
    }

    @Test
    void getConflictingRequestTaskTypes() {
        assertEquals(RequestTaskType.getRfiRdeWaitForResponseTypes(), validator.getConflictingRequestTaskTypes());
    }
}
