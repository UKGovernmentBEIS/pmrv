package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.AerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@ExtendWith(MockitoExtension.class)
class InitiateAersHandlerTest {

    @InjectMocks
    private InitiateAersHandler initiateAersHandler;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private AerCreationService aerCreationService;

    @Mock
    private DelegateExecution execution;

    @Test
    void executeAutomaticWorkflow() throws Exception {
        AccountDTO account1 = AccountDTO.builder().id(1L).name("Account 1").build();
        AccountDTO account2 = AccountDTO.builder().id(2L).name("Account 2").build();
        List<AccountDTO> accounts = List.of(account1, account2);

        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        when(execution.getProcessInstance()).thenReturn(delegateExecution);

        when(delegateExecution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(accountQueryService.getAccountsByStatus(AccountStatus.LIVE)).thenReturn(accounts);

        // Invoke
        initiateAersHandler.execute(execution);

        // Verify
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(account1);
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(account2);
        verifyNoMoreInteractions(aerCreationService);
    }

    @Test
    void executeManualWorkflow() throws Exception {
        AccountDTO account = AccountDTO.builder().id(1L).name("Account 1").build();

        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        when(execution.getProcessInstance()).thenReturn(delegateExecution);
        when(delegateExecution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(true);
        when(delegateExecution.getVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(Collections.singletonList(account.getId().toString()));
        when(accountQueryService.getAccountDTOById(account.getId())).thenReturn(account);

        // Invoke
        initiateAersHandler.execute(execution);

        // Verify
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(account);
        verifyNoMoreInteractions(aerCreationService);
    }

    @Test
    void execute_with_exception() throws Exception {
        AccountDTO account1 = AccountDTO.builder().id(1L).name("Account 1").build();
        AccountDTO account2 = AccountDTO.builder().id(2L).name("Account 2").build();
        AccountDTO account3 = AccountDTO.builder().id(3L).name("Account 3").build();
        List<AccountDTO> accounts = List.of(account1, account2, account3);

        DelegateExecution delegateExecution = mock(DelegateExecution.class);
        when(execution.getProcessInstance()).thenReturn(delegateExecution);

        when(delegateExecution.hasVariable(BpmnProcessConstants.ACCOUNT_IDS)).thenReturn(false);
        when(accountQueryService.getAccountsByStatus(AccountStatus.LIVE)).thenReturn(accounts);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(aerCreationService)
            .createRequestAer(account1);

        // Invoke
        initiateAersHandler.execute(execution);

        // Verify
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(account1);
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(account2);
        verify(aerCreationService, timeout(1000).times(1)).createRequestAer(account3);
        verifyNoMoreInteractions(aerCreationService);
    }
}
