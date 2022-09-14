package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRejectionServiceTest {

    @InjectMocks
    private AccountRejectionService service;

    @Mock
    private AccountStatusService accountStatusService;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    private AccountQueryService accountQueryService;

    @Test
    void rejectAccount() {
        final Long accountId = 1L;
        final Long legalEntityId = 2L;
        LegalEntity legalEntity = LegalEntity.builder().id(legalEntityId).build();
        Account account = Account.builder().id(accountId).legalEntity(legalEntity).build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);
        when(legalEntityService.getLegalEntityById(legalEntityId)).thenReturn(legalEntity);

        // Invoke
        service.rejectAccount(accountId);

        // Verify
        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(legalEntityService, times(1)).getLegalEntityById(legalEntityId);
        verify(accountStatusService, times(1)).handleInstallationAccountRejected(accountId);
        verify(legalEntityService, times(1)).handleLegalEntityDenied(legalEntity);
    }
}
