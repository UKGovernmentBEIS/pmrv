package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service.PermitIssuanceReviewService;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceSubmitApplicationAmendActionHandlerTest {

    @InjectMocks
    private PermitIssuanceSubmitApplicationAmendActionHandler submitApplicationAmendActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitIssuanceReviewService permitIssuanceReviewService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "123";
        PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload submitAmendRequestTaskActionPayload =
            PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                .permitSectionsCompleted(Map.of(
                    "installationCategory", List.of(true),
                    "installationOperatorDetails", List.of(true)
                ))
                .build();

        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().build())
            .processTaskId(processTaskId)
            .build();
        PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        submitApplicationAmendActionHandler
            .process(requestTask.getId(), RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND, pmrvUser, submitAmendRequestTaskActionPayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitIssuanceReviewService, times(1))
            .submitAmendedPermit(submitAmendRequestTaskActionPayload, requestTask);
        verify(requestService, times(1)).addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED,
            pmrvUser.getUserId());
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(submitApplicationAmendActionHandler.getTypes())
            .containsExactly(RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND);
    }
}