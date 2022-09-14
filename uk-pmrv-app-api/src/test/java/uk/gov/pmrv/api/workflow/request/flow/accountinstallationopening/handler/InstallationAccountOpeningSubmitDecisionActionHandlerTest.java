package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.APPLICATION_ACCEPTED;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningSubmitDecisionActionHandlerTest {

    @InjectMocks
    private InstallationAccountOpeningSubmitDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    private PmrvUser pmrvUser;

    @BeforeEach
    public void buildPmrvUser() {
        pmrvUser = PmrvUser.builder().userId("user").build();
    }

    @Test
    void doProcess_accepted_decision() {
        //prepare
        final String taskProcessId = "taskProcessId";
        final Decision decision = Decision.ACCEPTED;
        RequestTask requestTask = buildMockRequestTask(taskProcessId);
        InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload payload = buildAccountSubmitDecisionPayload(decision);
        AccountPayload accountPayload = new AccountPayload();
        AccountOpeningDecisionPayload
            accountOpeningDecisionPayload = AccountOpeningDecisionPayload.builder().decision(decision).build();
        InstallationAccountOpeningDecisionRequestActionPayload accountSubmittedDecisionPayload = InstallationAccountOpeningDecisionRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD)
            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
            .build();
        InstallationAccountOpeningApprovedRequestActionPayload accountApprovedPayload = InstallationAccountOpeningApprovedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD)
            .accountPayload(accountPayload)
            .build();
//        InstallationAccountOpeningRequestPayload instAccOpeningRequestPayload = InstallationAccountOpeningRequestPayload.builder()
//            .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
//            .accountPayload(accountPayload)
//            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
//            .build();
        
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION, pmrvUser, payload);
        
        //verify
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                accountSubmittedDecisionPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCEPTED,
                pmrvUser.getUserId());
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                accountApprovedPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED,
                pmrvUser.getUserId());
        verify(requestService, times(1)).saveRequest(requestTask.getRequest());
        verify(workflowService, times(1))
            .completeTask(taskProcessId, Map.of(APPLICATION_ACCEPTED, Boolean.TRUE));
    }

    @Test
    void doProcess_rejected_decision() {
        //prepare
        final String taskProcessId = "taskProcessId";
        final Decision decision = Decision.REJECTED;
        RequestTask requestTask = buildMockRequestTask(taskProcessId);
        InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload payload = buildAccountSubmitDecisionPayload(decision);
        AccountOpeningDecisionPayload
            accountOpeningDecisionPayload = AccountOpeningDecisionPayload.builder().decision(decision).build();
        InstallationAccountOpeningDecisionRequestActionPayload accountSubmittedDecisionPayload = InstallationAccountOpeningDecisionRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD)
            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
            .build();
//        InstallationAccountOpeningRequestPayload instAccOpeningRequestPayload = InstallationAccountOpeningRequestPayload.builder()
//            .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
//            .accountPayload(new AccountPayload())
//            .accountOpeningDecisionPayload(accountOpeningDecisionPayload)
//            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
        
        //invoke
        handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION, pmrvUser, payload);
        
        //verify
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                accountSubmittedDecisionPayload,
                RequestActionType.INSTALLATION_ACCOUNT_OPENING_REJECTED,
                pmrvUser.getUserId());
        verify(workflowService, times(1))
            .completeTask(taskProcessId, Map.of(APPLICATION_ACCEPTED, Boolean.FALSE));
        verify(requestService, times(1)).saveRequest(requestTask.getRequest());
        verifyNoMoreInteractions(requestService);
    }

    private RequestTask buildMockRequestTask(String taskProcessId) {
        return RequestTask.builder()
                .id(1L)
                .processTaskId(taskProcessId)
                .request(Request.builder()
                    .id("1")
                    .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
                    .competentAuthority(CompetentAuthority.WALES)
                    .status(RequestStatus.IN_PROGRESS)
                    .processInstanceId("process_id")
                    .payload(InstallationAccountOpeningRequestPayload.builder().build())
                    .build())
                .payload(InstallationAccountOpeningApplicationRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
                    .accountPayload(new AccountPayload())
                    .build())
                .build();
    }

    private InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload buildAccountSubmitDecisionPayload(Decision decision) {
        return InstallationAccountOpeningSubmitDecisionRequestTaskActionPayload.builder()
            .accountOpeningDecisionPayload(AccountOpeningDecisionPayload.builder().decision(decision).build())
            .build();
    }

}
