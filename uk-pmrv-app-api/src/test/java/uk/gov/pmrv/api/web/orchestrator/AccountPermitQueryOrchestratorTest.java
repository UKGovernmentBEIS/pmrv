package uk.gov.pmrv.api.web.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountPermitQueryOrchestratorTest {

    private static final Long TEST_ACCOUNT_ID = 1L;
    private static final String TEST_PERMIT_ID = "UK-E-IN-00001";
    @InjectMocks
    private AccountPermitQueryOrchestrator orchestrator;

    @Mock
    private AccountQueryService accountQueryService;
    @Mock
    private PermitQueryService permitQueryService;


    @Test
    void shouldAggregateAccountDetailsWithPermitId() {

        AccountDetailsDTO accountDetailsDTO = AccountDetailsDTO.builder()
            .build();
        when(accountQueryService.getAccountDetailsDTOById(TEST_ACCOUNT_ID)).thenReturn(accountDetailsDTO);
        when(permitQueryService.getPermitIdByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.of(TEST_PERMIT_ID));
        AccountDetailsDTO finalDetails = orchestrator.getAccountDetailsDtoWithPermit(TEST_ACCOUNT_ID);

        assertThat(finalDetails.getPermitId()).isEqualTo(TEST_PERMIT_ID);
    }

    @Test
    void shouldAggregateAccountDetailsWithoutPermitIdWhenNotPresent() {

        AccountDetailsDTO accountDetailsDTO = AccountDetailsDTO.builder()
            .build();
        when(accountQueryService.getAccountDetailsDTOById(TEST_ACCOUNT_ID)).thenReturn(accountDetailsDTO);
        when(permitQueryService.getPermitIdByAccountId(TEST_ACCOUNT_ID)).thenReturn(Optional.empty());
        AccountDetailsDTO finalDetails = orchestrator.getAccountDetailsDtoWithPermit(TEST_ACCOUNT_ID);

        assertThat(finalDetails.getPermitId()).isNull();
    }
}
