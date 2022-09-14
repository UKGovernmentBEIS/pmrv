package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;

@ExtendWith(MockitoExtension.class)
class PermitReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private PermitReviewSaveGroupDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitIssuanceReviewService permitIssuanceReviewService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload payload =
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .group(PermitReviewGroup.INSTALLATION_DETAILS)
                .decision(PermitIssuanceReviewDecision.builder()
                    .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .requiredChange(new PermitReviewDecisionRequiredChange("changesRequired", Set.of(UUID.randomUUID())))
                    .notes("notes")
                    .build())
                .reviewSectionsCompleted(Map.of("section1", true))
                .build();

        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
                        RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION,
                        pmrvUser,
                        payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitIssuanceReviewService, times(1)).saveReviewGroupDecision(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION);
    }
}