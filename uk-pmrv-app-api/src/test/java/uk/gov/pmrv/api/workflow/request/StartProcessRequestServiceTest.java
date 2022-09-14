package uk.gov.pmrv.api.workflow.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestCreateService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.FallbackRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestIdGeneratorMapper;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;

@ExtendWith(MockitoExtension.class)
class StartProcessRequestServiceTest {

    @InjectMocks
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestCreateService requestCreateService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestIdGeneratorMapper mapper;

    @Mock
    private FallbackRequestIdGenerator fallbackRequestIdGenerator;

    @BeforeEach
    void setUp() {
        when(mapper.get(any())).thenReturn(fallbackRequestIdGenerator);
    }

    @Test
    void startProcess() {
        final String requestId = "1";
        final RequestType requestType = RequestType.INSTALLATION_ACCOUNT_OPENING;
        final RequestParams requestParams = RequestParams.builder()
                .type(requestType)
                .ca(CompetentAuthority.ENGLAND)
                .accountId(1L)
                .processVars(Map.of(
                		"proccVar1", "processVar1Val"
                		))
                .build();
        final String processInstanceId = "prInstanceId";

        Request request = Request.builder().id(requestId).type(requestType).build();

        when(requestCreateService.createRequest(requestParams, IN_PROGRESS))
            .thenReturn(request);

        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.REQUEST_ID, request.getId());
        processVars.put(BpmnProcessConstants.REQUEST_TYPE, request.getType().name());
        processVars.putAll(requestParams.getProcessVars());

        when(workflowService.startProcessDefinition(requestType.getProcessDefinitionId(), processVars))
            .thenReturn(processInstanceId);

        // Invoke
        Request result = startProcessRequestService.startProcess(requestParams);

        //assert
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getProcessInstanceId()).isEqualTo(processInstanceId);
        // Verify
        verify(requestCreateService, times(1)).createRequest(requestParams, IN_PROGRESS);
        verify(workflowService, times(1)).startProcessDefinition(requestType.getProcessDefinitionId(), processVars);
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());
        Request requestSaved = requestCaptor.getValue();
        assertThat(requestSaved.getProcessInstanceId()).isEqualTo(processInstanceId);
    }

    @Test
    void startSystemMessageNotificationProcess() {
        final String requestId = "1";
        final String assignee = "userId";
        final RequestType requestType = RequestType.SYSTEM_MESSAGE_NOTIFICATION;
        final RequestTaskType requestTaskType = RequestTaskType.ACCOUNT_USERS_SETUP;
        final SystemMessageNotificationRequestPayload requestPayload = SystemMessageNotificationRequestPayload.builder()
            .operatorAssignee(assignee)
            .build();
        final RequestParams requestParams = RequestParams.builder()
            .type(requestType)
            .ca(ENGLAND)
            .accountId(1L)
            .requestPayload(requestPayload)
            .build();
        final String processInstanceId = "prInstanceId";

        Request request = Request.builder().id(requestId).type(requestType).payload(requestPayload).build();

        when(requestCreateService.createRequest(requestParams, IN_PROGRESS))
            .thenReturn(request);

        Map<String, Object> processVars = Map.of(
            BpmnProcessConstants.REQUEST_ID, request.getId(),
            BpmnProcessConstants.REQUEST_TASK_TYPE, requestTaskType,
            BpmnProcessConstants.REQUEST_TASK_ASSIGNEE, assignee
        );
        when(workflowService.startProcessDefinition(requestType.getProcessDefinitionId(), processVars))
            .thenReturn(processInstanceId);


        // Invoke
        Request result =
            startProcessRequestService.startSystemMessageNotificationProcess(requestParams, requestTaskType);

        //assert
        assertThat(result.getId()).isEqualTo(requestId);
        assertThat(result.getProcessInstanceId()).isEqualTo(processInstanceId);
        // Verify
        verify(requestCreateService, times(1)).createRequest(requestParams, IN_PROGRESS);
        verify(workflowService, times(1)).startProcessDefinition(requestType.getProcessDefinitionId(), processVars);
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());
        Request requestSaved = requestCaptor.getValue();
        assertThat(requestSaved.getProcessInstanceId()).isEqualTo(processInstanceId);
    }

}
