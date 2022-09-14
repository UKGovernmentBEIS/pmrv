package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountStatusServiceTest {

    @InjectMocks
    private AccountStatusService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private ApplicationEventPublisher publisher;

    @Test
    void handlePermitSurrenderGranted() {
        Long accountId = 1L;

        Account account = Account.builder().id(accountId).status(AccountStatus.LIVE).build();

        when(accountQueryService.getAccountById(accountId)).thenReturn(account);

        service.handlePermitSurrenderGranted(accountId);

        assertThat(account.getStatus()).isEqualTo(AccountStatus.AWAITING_SURRENDER);
        verify(accountQueryService, times(1)).getAccountById(accountId);
        verify(accountRepository, times(1)).save(account);
    }
}
