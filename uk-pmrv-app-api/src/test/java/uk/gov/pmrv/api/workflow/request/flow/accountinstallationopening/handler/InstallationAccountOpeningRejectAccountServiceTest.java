package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountRejectionService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningRejectAccountServiceTest {

    @InjectMocks
    private InstallationAccountOpeningRejectAccountService installationAccountOpeningRejectAccountService;

    @Mock
    private RequestService requestService;

    @Mock
    private AccountRejectionService accountRejectionService;

    @Test
    void execute() {
        //prepare data
        final String requestId = "1";
        final Long accountId = 1L;
        final Request request = Request.builder().id(requestId).accountId(accountId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        //invoke
        installationAccountOpeningRejectAccountService.execute(requestId);

        //verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountRejectionService, times(1)).rejectAccount(accountId);
    }
}
