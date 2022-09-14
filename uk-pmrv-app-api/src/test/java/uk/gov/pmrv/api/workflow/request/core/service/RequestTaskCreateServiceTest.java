package uk.gov.pmrv.api.workflow.request.core.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskDefaultAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler.InstallationAccountOpeningApplicationReviewInitializer;

@ExtendWith(MockitoExtension.class)
class RequestTaskCreateServiceTest {

    private static final String REQUEST_ID = "1";
    private static final String PROCESS_INSTANCE_ID = "process_instance_id";
    private static final String PROCESS_TASK_ID = "process_task_id";

    @InjectMocks
    private RequestTaskCreateService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskDefaultAssignmentService requestTaskDefaultAssignmentService;

    @Mock
    private InstallationAccountOpeningApplicationReviewInitializer installationAccountOpeningApplicationReviewInitializer;

    @Spy
    private ArrayList<InitializeRequestTaskHandler> initializeRequestTaskHandlers;

    @BeforeEach
    public void setUp() {
        initializeRequestTaskHandlers.add(installationAccountOpeningApplicationReviewInitializer);
    }
    
    @Test
    void create_assign_to_user_provided() {
        final RequestTaskType type = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
        String userToAssignTask = "assignee";
        Request request = createRequest();
        InstallationAccountOpeningApplicationRequestTaskPayload instAccOpeningApplicationRequestTaskPayload =
            InstallationAccountOpeningApplicationRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
                .accountPayload(AccountPayload.builder().accountType(AccountType.INSTALLATION).build())
                .build();
        
        when(requestService.findRequestById(REQUEST_ID)).thenReturn(request);
        when(installationAccountOpeningApplicationReviewInitializer.getRequestTaskTypes()).thenReturn(Set.of(type));
        when(installationAccountOpeningApplicationReviewInitializer.initializePayload(request)).thenReturn(instAccOpeningApplicationRequestTaskPayload);

        //invoke
        service.create(REQUEST_ID, PROCESS_TASK_ID, type, userToAssignTask);

        //verify
        verify(requestService, times(1)).findRequestById(REQUEST_ID);
        verify(requestTaskDefaultAssignmentService, never()).assignDefaultAssigneeToTask(Mockito.any());
    }
    
    @Test
    void create_assign_to_default() {
        final RequestTaskType type = RequestTaskType.ACCOUNT_USERS_SETUP;
        Request request = createRequest();
        RequestTask requestTask = createRequestTask(request, PROCESS_TASK_ID, type.name());

        when(requestService.findRequestById(REQUEST_ID)).thenReturn(request);

        //invoke
        service.create(REQUEST_ID, PROCESS_TASK_ID, type);

        //verify
        verify(requestService, times(1)).findRequestById(REQUEST_ID);
        verify(requestTaskDefaultAssignmentService, times(1)).assignDefaultAssigneeToTask(requestTask);
    }

    private Request createRequest() {
        Request request = new Request();
        request.setId(REQUEST_ID);
        request.setProcessInstanceId(PROCESS_INSTANCE_ID);
        return request;
    }

    private RequestTask createRequestTask(Request request, String processTaskId, String taskDefinitionKey) {
        return RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .type(RequestTaskType.valueOf(taskDefinitionKey))
                .build();
    }

}
