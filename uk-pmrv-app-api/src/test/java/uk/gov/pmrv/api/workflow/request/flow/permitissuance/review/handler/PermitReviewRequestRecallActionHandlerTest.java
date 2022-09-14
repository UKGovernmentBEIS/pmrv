package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_ISSUANCE_RECALLED_FROM_AMENDS;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class PermitReviewRequestRecallActionHandlerTest {

    @InjectMocks
    private PermitReviewRequestRecallActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestService requestService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        final Request build = Request.builder().id("2").build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(build)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_ISSUANCE_RECALL_FROM_AMENDS,
            pmrvUser,
            new RequestTaskActionEmptyPayload());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                null,
                PERMIT_ISSUANCE_RECALLED_FROM_AMENDS,
                pmrvUser.getUserId());
        verify(workflowService, times(1)).completeTask("processTaskId");
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_ISSUANCE_RECALL_FROM_AMENDS);
    }
}
