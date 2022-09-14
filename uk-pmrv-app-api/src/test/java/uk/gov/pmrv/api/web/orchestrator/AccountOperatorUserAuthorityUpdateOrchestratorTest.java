package uk.gov.pmrv.api.web.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactUpdateService;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.operator.domain.NewUserActivated;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityUpdateService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserNotificationGateway;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountOperatorUserAuthorityUpdateOrchestratorTest {

    @InjectMocks
    private AccountOperatorUserAuthorityUpdateOrchestrator service;

    @Mock
    private OperatorAuthorityUpdateService operatorAuthorityUpdateService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Mock
    private AccountContactUpdateService accountContactUpdateService;

    @Test
    void updateAccountOperatorAuthorities() {
        Long accountId = 1L;
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities = List.of(
                AccountOperatorAuthorityUpdateDTO.builder().userId("user").roleCode("newRole").authorityStatus(AuthorityStatus.ACTIVE).build()
        );

        Map<AccountContactType, String> updatedContactTypes = Map.of(AccountContactType.SECONDARY, "user");
        List<NewUserActivated> activatedOperators = List.of(NewUserActivated.builder().userId("user").build());

        when(operatorAuthorityUpdateService.updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId))
                .thenReturn(activatedOperators);

        service.updateAccountOperatorAuthorities(accountOperatorAuthorities, updatedContactTypes, accountId);

        verify(operatorAuthorityUpdateService, times(1))
                .updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);
        verify(accountContactUpdateService, times(1)).updateAccountContacts(updatedContactTypes, accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyUsersUpdateStatus(activatedOperators);
    }

    @Test
    void updateAccountOperatorAuthorities_empty_notifications() {
        Long accountId = 1L;
        List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorities = List.of(
                AccountOperatorAuthorityUpdateDTO.builder().userId("user").roleCode("newRole").authorityStatus(AuthorityStatus.ACTIVE).build()
        );
        Map<AccountContactType, String> updatedContactTypes = Map.of(AccountContactType.SECONDARY, "user");

        when(operatorAuthorityUpdateService.updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId))
                .thenReturn(List.of());

        service.updateAccountOperatorAuthorities(accountOperatorAuthorities, updatedContactTypes, accountId);

        verify(operatorAuthorityUpdateService, times(1))
                .updateAccountOperatorAuthorities(accountOperatorAuthorities, accountId);
        verify(accountContactUpdateService, times(1)).updateAccountContacts(updatedContactTypes, accountId);
        verify(operatorUserNotificationGateway, never()).notifyUsersUpdateStatus(anyList());
    }
}
