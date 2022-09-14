package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountActivationServiceTest {

    @InjectMocks
    private AccountActivationService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    private OperatorAuthorityService operatorAuthorityService;

    @Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private AccountContactUpdateService accountContactUpdateService;

    @Mock
    private AccountStatusService accountStatusService;

    @Test
    void activateAccount() {
        final Long accountId = 1L;
        String user = "assignee";
        
        LegalEntityDTO legalEntityDTO = LegalEntityDTO.builder().id(1L).name("le").build();
        AccountDTO accountDTO = AccountDTO.builder()
                .name("account")
                .legalEntity(legalEntityDTO)
                .build();
        
        LegalEntity legalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .status(LegalEntityStatus.PENDING)
                .build();
        Account account = Account.builder()
                .id(accountId)
                .name("account")
                .legalEntity(legalEntity)
                .build();
        
        LegalEntity activatedLegalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .status(LegalEntityStatus.ACTIVE)
                .build();
        
        when(accountQueryService.getAccountById(accountId)).thenReturn(account);
        when(legalEntityService.activateLegalEntity(accountDTO.getLegalEntity())).thenReturn(activatedLegalEntity);
        
        //invoke
        service.activateAccount(accountId, accountDTO, user);
        
        //verify
        verify(accountQueryService, times(1)).validateAccountName(accountDTO.getName());
        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(legalEntityService, times(1)).activateLegalEntity(accountDTO.getLegalEntity());
        verify(accountStatusService, times(1)).handleInstallationAccountAccepted(accountId);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getAcceptedDate()).isNotNull();
        assertThat(accountCaptured.getLegalEntity()).isEqualTo(activatedLegalEntity);
        
        verify(operatorAuthorityService, times(1)).createOperatorAdminAuthority(accountId, user);
        verify(accountContactUpdateService, times(1)).assignUserAsDefaultAccountContactPoint(user, accountCaptured);
    }
    
}
