package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.ACCOUNT_APPLICATION_RECEIVED;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.service.AccountCreationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.mapper.AccountPayloadMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningSubmitApplicationCreateActionHandler 
    implements RequestCreateActionHandler<InstallationAccountOpeningSubmitApplicationCreateActionPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final NotificationEmailService notificationEmailService;
    private final RequestService requestService;
    private final AccountCreationService accountCreationService;
    private static final AccountPayloadMapper accountPayloadMapper = Mappers.getMapper(AccountPayloadMapper.class);

    @Override
    public String process(final Long accountId,
            final RequestCreateActionType type,
            final InstallationAccountOpeningSubmitApplicationCreateActionPayload payload, 
            final PmrvUser pmrvUser) {
        final AccountPayload accountPayload = payload.getAccountPayload();

        AccountDTO accountDTO = accountPayloadMapper.toAccountDTO(accountPayload);
        
        //create account
        accountDTO = accountCreationService.createAccount(accountDTO, pmrvUser);
        
        // enhance account payload with full legal entity info if id only provided
        if(accountPayload.getLegalEntity().getId() != null) {
            accountPayload.setLegalEntity(accountDTO.getLegalEntity());
        }

        //create request and start flow
        Request request = startProcessRequestService.startProcess(
            RequestParams.builder()
                .type(INSTALLATION_ACCOUNT_OPENING)
                .ca(accountPayload.getCompetentAuthority())
                .accountId(accountDTO.getId())
                .requestPayload(accountPayloadMapper.toInstallationAccountOpeningRequestPayload(accountPayload, pmrvUser.getUserId()))
                .build()
        );
        
        //set request's submission date
        request.setSubmissionDate(request.getCreationDate());

        //create request action
        requestService.addActionToRequest(
            request,
            accountPayloadMapper.toInstallationAccountOpeningApplicationSubmittedRequestActionPayload(accountPayload),
            RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
            pmrvUser.getUserId());

        //notify recipient
        notificationEmailService.notifyRecipient(
                EmailData.builder()
                    .notificationTemplateData(EmailNotificationTemplateData.builder()
                            .templateName(ACCOUNT_APPLICATION_RECEIVED)
                            .build())
                    .build(),
                pmrvUser.getEmail());
        
        return request.getId();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION;
    }

}
