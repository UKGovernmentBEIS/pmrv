package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.REQUEST_ID;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

@ExtendWith(MockitoExtension.class)
class DismissActionHandlerTest {

    @InjectMocks
    private DismissActionHandler handler;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestTaskService requestTaskService;

    private PmrvUser pmrvUser;

    @BeforeEach
    public void buildPmrvUser() {
        pmrvUser = PmrvUser.builder().userId("user").build();
    }

    @Test
    void doProcess() {

        RequestTask requestTask = buildMockRequestTask();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE, pmrvUser, new RequestTaskActionEmptyPayload());
        
        //verify
        verify(workflowService, times(1))
                .completeTask(requestTask.getProcessTaskId(), Map.of(REQUEST_ID, requestTask.getRequest().getId()));
    }

    private RequestTask buildMockRequestTask() {
        return RequestTask.builder()
                .id(1L)
                .request(Request.builder()
                        .id("10")
                        .type(RequestType.SYSTEM_MESSAGE_NOTIFICATION)
                        .competentAuthority(CompetentAuthority.WALES)
                        .status(RequestStatus.IN_PROGRESS)
                        .processInstanceId("abc")
                        .build())
                .build();
    }
}
