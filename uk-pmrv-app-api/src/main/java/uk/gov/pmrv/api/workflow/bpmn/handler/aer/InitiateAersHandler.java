package uk.gov.pmrv.api.workflow.bpmn.handler.aer;

import static uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants.ACCOUNT_IDS;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.AerCreationService;

/**
 * Initiates AER for </br>
 *
 * <ul>
 *     <li>LIVE accounts when the associated timer in Camunda has been executed</li>
 *     <li>OR for the provided account ids through the Camunda REST API. It is useful when some AERs have not been successfully executed
 *     when the timer kicked in.</li>
 * </ul>
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class InitiateAersHandler implements JavaDelegate {

    private final AccountQueryService accountQueryService;
    private final AerCreationService aerCreationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        getAccounts(execution).forEach(account -> CompletableFuture.runAsync(() -> initiateAerWorkflow(account)));
    }

    private List<AccountDTO> getAccounts(DelegateExecution execution) {
        if (!execution.getProcessInstance().hasVariable(ACCOUNT_IDS)) {
            return accountQueryService.getAccountsByStatus(AccountStatus.LIVE);
        }
        List<String> providedAccountIds = (List<String>) execution.getProcessInstance().getVariable(ACCOUNT_IDS);
        return getAccounts(providedAccountIds);
    }

    private List<AccountDTO> getAccounts(List<String> providedAccountIds) {
        return providedAccountIds
            .stream()
            .map(accountId -> Long.parseLong(accountId.trim()))
            .map(accountQueryService::getAccountDTOById)
            .collect(Collectors.toList());
    }

    private void initiateAerWorkflow(AccountDTO account) {
        try {
            aerCreationService.createRequestAer(account);
        } catch (Exception ex) {
            log.error("Could not create AER workflow for account with id '{}' failed with {}",
                account::getId, () -> ExceptionUtils.getRootCause(ex).getMessage());
        }
    }
}
