package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;

@ExtendWith(MockitoExtension.class)
class PermitReviewSaveDeterminationActionHandlerTest {

    @InjectMocks
    private PermitReviewSaveDeterminationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitIssuanceReviewService permitIssuanceReviewService;

    @Mock
    private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationAndDecisionsValidatorService;

    
    @Test
    void process_whenValid_thenSave() {
        final Long requestTaskId = 1L;
        final PermitIssuanceGrantDetermination determination = PermitIssuanceGrantDetermination.builder()
                .type(DeterminationType.GRANTED)
                .reason("reason")
                .activationDate(LocalDate.of(2030, 1, 1))
                .build();
        final PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload payload =
            PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                .determination(determination)
                .reviewSectionsCompleted(Map.of("section1", Boolean.TRUE))
                .build();
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId)
        		.payload(taskPayload)
        		.request(Request.builder().type(RequestType.PERMIT_ISSUANCE).build())
        		.build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(permitReviewDeterminationAndDecisionsValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE)).thenReturn(true);
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId,
            RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION,
            pmrvUser,
            payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitReviewDeterminationAndDecisionsValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE);
        verify(permitIssuanceReviewService, times(1)).saveDetermination(payload, requestTask);
    }

    @Test
    void process_whenInvalid_thenDoNotSave() {
        final Long requestTaskId = 1L;
        final PermitIssuanceGrantDetermination determination = PermitIssuanceGrantDetermination.builder()
                .type(DeterminationType.GRANTED)
                .reason("reason")
                .activationDate(LocalDate.of(2030, 1, 1))
                .build();
        final PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload payload =
            PermitIssuanceSaveReviewDeterminationRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD)
                .determination(determination)
                .reviewSectionsCompleted(Map.of("section1", Boolean.TRUE))
                .build();
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId)
        		.payload(taskPayload)
        		.request(Request.builder().type(RequestType.PERMIT_ISSUANCE).build())
        		.build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(permitReviewDeterminationAndDecisionsValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE)).thenReturn(false);
        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        final BusinessException businessException = assertThrows(BusinessException.class,
            () -> handler.process(requestTaskId,
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION,
                pmrvUser,
                payload));

        assertThat(businessException.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitReviewDeterminationAndDecisionsValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, taskPayload, RequestType.PERMIT_ISSUANCE);
        verify(permitIssuanceReviewService, never()).saveDetermination(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION);
    }
}