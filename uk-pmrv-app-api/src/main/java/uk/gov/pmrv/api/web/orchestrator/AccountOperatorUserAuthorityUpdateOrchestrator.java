package uk.gov.pmrv.api.web.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactUpdateService;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.operator.domain.NewUserActivated;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityUpdateService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserNotificationGateway;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountOperatorUserAuthorityUpdateOrchestrator {

    private final OperatorAuthorityUpdateService operatorAuthorityUpdateService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;
    private final AccountContactUpdateService accountContactUpdateService;

    public void updateAccountOperatorAuthorities(List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities,
                                                 Map<AccountContactType, String> updatedContactTypes, Long accountId) {

        List<NewUserActivated> activatedOperators = operatorAuthorityUpdateService
                .updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);

        accountContactUpdateService.updateAccountContacts(updatedContactTypes, accountId);

        if(!activatedOperators.isEmpty()){
            operatorUserNotificationGateway.notifyUsersUpdateStatus(activatedOperators);
        }
    }
}
