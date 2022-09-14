package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.service.AccountActivationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.mapper.AccountPayloadMapper;

@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningActivateAccountService {
    private final AccountPayloadMapper accountPayloadMapper;
    private final AccountActivationService accountActivationService;
    private final RequestService requestService;

    public void execute(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final InstallationAccountOpeningRequestPayload instAccOpeningRequestPayload =
            (InstallationAccountOpeningRequestPayload) request.getPayload();
        AccountDTO accountDTO = accountPayloadMapper.toAccountDTO(instAccOpeningRequestPayload.getAccountPayload());
        accountActivationService.activateAccount(request.getAccountId(), accountDTO, instAccOpeningRequestPayload.getOperatorAssignee());
    }
}
