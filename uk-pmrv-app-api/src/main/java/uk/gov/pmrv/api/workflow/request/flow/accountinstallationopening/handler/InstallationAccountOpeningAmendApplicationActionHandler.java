package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.service.AccountAmendService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskValidationService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningAmendApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.mapper.AccountPayloadMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningAmendApplicationActionHandler
    implements RequestTaskActionHandler<InstallationAccountOpeningAmendApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final AccountAmendService accountAmendService;
    private final RequestTaskValidationService requestTaskValidationService;
    private static final AccountPayloadMapper accountPayloadMapper = Mappers.getMapper(AccountPayloadMapper.class);

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final InstallationAccountOpeningAmendApplicationRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        Request request = requestTask.getRequest();
        
        AccountPayload currentAccountPayload =
                ((InstallationAccountOpeningApplicationRequestTaskPayload) requestTask.getPayload()).getAccountPayload();
        AccountDTO currentAccountDTO = accountPayloadMapper.toAccountDTO(currentAccountPayload);
        
        AccountPayload newAccountPayload = payload.getAccountPayload();
        AccountDTO newAccountDTO = accountPayloadMapper.toAccountDTO(newAccountPayload);
        
        //amend account
        newAccountDTO = accountAmendService.amendAccount(request.getAccountId(), currentAccountDTO, newAccountDTO, pmrvUser);

        // enhance account payload with full legal entity info if id only provided
        if(newAccountPayload.getLegalEntity().getId() != null) {
            newAccountPayload.setLegalEntity(newAccountDTO.getLegalEntity());
        }

        //update request task payload with new account payload
        InstallationAccountOpeningApplicationRequestTaskPayload newInstallationAccountOpeningApplicationRequestTaskPayload = accountPayloadMapper
                .toInstallationAccountOpeningApplicationRequestTaskPayload(newAccountPayload);
        requestTaskValidationService.validateRequestTaskPayload(newInstallationAccountOpeningApplicationRequestTaskPayload);
        requestTaskService.updateRequestTaskPayload(requestTask,
                newInstallationAccountOpeningApplicationRequestTaskPayload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION);
    }
}
