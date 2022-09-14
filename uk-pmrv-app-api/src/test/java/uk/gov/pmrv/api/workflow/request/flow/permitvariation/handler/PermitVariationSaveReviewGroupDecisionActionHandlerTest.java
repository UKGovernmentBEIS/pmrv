package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationReviewService;

@ExtendWith(MockitoExtension.class)
public class PermitVariationSaveReviewGroupDecisionActionHandlerTest {

    @InjectMocks
    private PermitVariationSaveReviewGroupDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationReviewService permitVariationReviewService;

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION);
    }

    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION;
        PmrvUser pmrvUser = PmrvUser.builder().build();
        PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload payload = PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload
            .builder().build();

        RequestTask requestTask = RequestTask.builder().id(1L).build();
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, pmrvUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(permitVariationReviewService, times(1)).saveReviewGroupDecision(payload, requestTask);
    }

}
