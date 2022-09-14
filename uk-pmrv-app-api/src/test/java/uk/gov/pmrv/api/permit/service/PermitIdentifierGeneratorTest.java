package uk.gov.pmrv.api.permit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@ExtendWith(MockitoExtension.class)
class PermitIdentifierGeneratorTest {

    private static final Long TEST_ACCOUNT_ID = 1L;

    @InjectMocks
    private PermitIdentifierGenerator cut;

    @Mock
    private AccountQueryService accountQueryService;

    @Test
    void shouldGeneratePermitIdForInstallationInEngland() {
        Account account = Account.builder()
            .id(TEST_ACCOUNT_ID)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .accountType(AccountType.INSTALLATION)
            .build();
        when(accountQueryService.getApprovedAccountById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(account));
        String permitId = cut.generate(TEST_ACCOUNT_ID);

        assertThat(permitId).isEqualTo("UK-E-IN-00001");
    }

    @Test
    void shouldGeneratePermitIdForAviationInWales() {
        Account account = Account.builder()
            .id(TEST_ACCOUNT_ID)
            .competentAuthority(CompetentAuthority.WALES)
            .accountType(AccountType.AVIATION)
            .build();
        when(accountQueryService.getApprovedAccountById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(account));
        String permitId = cut.generate(TEST_ACCOUNT_ID);

        assertThat(permitId).isEqualTo("UK-W-AV-00001");
    }
}
