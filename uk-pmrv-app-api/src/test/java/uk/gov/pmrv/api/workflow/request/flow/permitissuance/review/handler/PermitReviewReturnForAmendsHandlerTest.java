package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.validation.PermitReviewReturnForAmendsValidatorService;

@ExtendWith(MockitoExtension.class)
class PermitReviewReturnForAmendsHandlerTest {

    @InjectMocks
    private PermitReviewReturnForAmendsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitIssuanceReviewService permitIssuanceReviewService;

    @Mock
    private PermitReviewReturnForAmendsValidatorService permitReviewReturnForAmendsValidatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void doProcess() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        PermitIssuanceReviewDecision reviewAmendDecision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
            .requiredChange(new PermitReviewDecisionRequiredChange("changesRequired", Collections.emptySet())).build();
        PermitIssuanceReviewDecision reviewNoAmendDecision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.ACCEPTED)
            .requiredChange(new PermitReviewDecisionRequiredChange("changesRequired", Collections.emptySet())).build();
        PermitIssuanceApplicationReviewRequestTaskPayload payload =
                PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                        .reviewGroupDecisions(Map.of(
                                PermitReviewGroup.CALCULATION, reviewAmendDecision,
                                PermitReviewGroup.FALLBACK, reviewNoAmendDecision
                        )).build();

        Request request = Request.builder()
                .id("2")
                .payload(PermitIssuanceRequestPayload.builder().build())
                .build();
        RequestTask requestTask = RequestTask.builder()
                .id(taskId)
                .processTaskId(processTaskId)
                .payload(payload)
                .request(request)
                .build();

        PermitIssuanceApplicationReturnedForAmendsRequestActionPayload actionPayload =
                PermitIssuanceApplicationReturnedForAmendsRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD)
                        .reviewGroupDecisions(Map.of(PermitReviewGroup.CALCULATION, reviewAmendDecision))
                        .build();

        PermitIssuanceRequestPayload newPermitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
                .reviewGroupDecisions(Map.of(PermitReviewGroup.CALCULATION, reviewAmendDecision))
                .build();
        Request newRequest = Request.builder()
                .id("2")
                .payload(newPermitIssuanceRequestPayload)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);

        // Invoke
        handler.process(taskId, RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS, pmrvUser, emptyPayload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(permitReviewReturnForAmendsValidatorService, times(1)).validate(payload);
        verify(permitIssuanceReviewService, times(1)).saveRequestReturnForAmends(requestTask, pmrvUser);
        verify(requestService, times(1))
                .addActionToRequest(newRequest, actionPayload, RequestActionType.PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS, userId);
        verify(workflowService, times(1))
                .completeTask(processTaskId, Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Test
    void doProcess_not_valid() {
        long taskId = 1L;
        RequestTaskActionEmptyPayload emptyPayload = RequestTaskActionEmptyPayload.builder().payloadType(RequestTaskActionPayloadType.EMPTY_PAYLOAD).build();
        String userId = "userId";
        PmrvUser pmrvUser = PmrvUser.builder().userId(userId).build();

        String processTaskId = "processTaskId";

        PermitIssuanceApplicationReviewRequestTaskPayload payload =
                PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                        .reviewGroupDecisions(Map.of(
                        		PermitReviewGroup.CALCULATION, PermitIssuanceReviewDecision.builder().build()
                        )).build();

        RequestTask requestTask = RequestTask.builder()
                .id(taskId)
                .processTaskId(processTaskId)
                .payload(payload)
                .request(Request.builder().build())
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        doThrow(new BusinessException((ErrorCode.INVALID_PERMIT_REVIEW))).when(permitReviewReturnForAmendsValidatorService)
                .validate(payload);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> handler.process(taskId, RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS, pmrvUser, emptyPayload));

        // Verify
        assertEquals(ErrorCode.INVALID_PERMIT_REVIEW, businessException.getErrorCode());
        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(permitReviewReturnForAmendsValidatorService, times(1)).validate(payload);
        verify(permitIssuanceReviewService, never()).saveRequestReturnForAmends(any(),any());
        verify(requestService, never()).addActionToRequest(any(), any(), any(), anyString());
        verify(workflowService, never()).completeTask(anyString(), anyMap());
    }
}
