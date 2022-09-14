package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.service.AccountActivationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.mapper.AccountPayloadMapper;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningActivateAccountServiceTest {

    @InjectMocks
    private InstallationAccountOpeningActivateAccountService installationAccountOpeningActivateAccountService;

    @Mock
    private RequestService requestService;

    @Mock
    private AccountActivationService accountActivationService;

    @Mock
    private AccountPayloadMapper accountPayloadMapper;

    @Test
    void execute() {
        //prepare data
        final String requestId = "1";
        final Long accountId = 1L;
        final String assignee = "user";
        final AccountPayload accountPayload = new AccountPayload();
        final Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .payload(InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountPayload(accountPayload)
                .operatorAssignee(assignee)
                .build())
            .build();
        
        final AccountDTO accountDTO = new AccountDTO();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(accountPayloadMapper.toAccountDTO(accountPayload)).thenReturn(accountDTO);

        //invoke
        installationAccountOpeningActivateAccountService.execute(requestId);

        //verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(accountPayloadMapper, times(1)).toAccountDTO(accountPayload);
        verify(accountActivationService, times(1)).activateAccount(accountId, accountDTO, assignee);
    }
}
