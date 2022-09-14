package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.RequestPermitSurrenderReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private PermitSurrenderReviewSaveGroupDecisionActionHandler handler;

    @Mock
    private RequestPermitSurrenderReviewService requestPermitSurrenderReviewService;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Test
    void process() {
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
        final PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload payload = PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .reviewDecision(PermitSurrenderReviewDecision.builder().type(PermitSurrenderReviewDecisionType.ACCEPTED).notes("notes").build())
                .build();
        
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTaskId, requestTaskActionType, pmrvUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(requestPermitSurrenderReviewService, times(1)).saveReviewDecision(payload, requestTask);
    }
    
    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION);
    }
}
