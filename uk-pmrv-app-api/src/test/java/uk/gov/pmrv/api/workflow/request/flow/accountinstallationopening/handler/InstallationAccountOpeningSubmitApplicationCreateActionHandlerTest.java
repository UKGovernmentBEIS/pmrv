package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.ACCOUNT_APPLICATION_RECEIVED;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountCreationService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningSubmitApplicationCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningSubmitApplicationCreateActionHandlerTest {

    @InjectMocks
    private InstallationAccountOpeningSubmitApplicationCreateActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private NotificationEmailService notificationEmailService;

    @Mock
    private AccountCreationService accountCreationService;

    @Test
    void process() {
        PmrvUser pmrvUser = PmrvUser.builder().userId("user").firstName("fn").lastName("ln").build();
        Long accountId = 1L;
        AccountPayload accountPayload = AccountPayload.builder()
                .accountType(AccountType.INSTALLATION)
                .name("account")
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(LocationOnShoreDTO.builder().build())
                .legalEntity(LegalEntityDTO.builder().id(1L).build()).build();
        
        InstallationAccountOpeningSubmitApplicationCreateActionPayload createActionPayload = InstallationAccountOpeningSubmitApplicationCreateActionPayload
                .builder()
                .payloadType(RequestCreateActionPayloadType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION_PAYLOAD)
                .accountPayload(accountPayload).build();
        
        InstallationAccountOpeningRequestPayload requestPayload = InstallationAccountOpeningRequestPayload.builder()
            .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
            .accountPayload(accountPayload)
            .operatorAssignee(pmrvUser.getUserId())
            .build();
        
        InstallationAccountOpeningApplicationSubmittedRequestActionPayload accountSubmittedPayload = InstallationAccountOpeningApplicationSubmittedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD)
                .accountPayload(accountPayload)
                .build();
        
        AccountDTO accountDTO = AccountDTO.builder()
                .accountType(AccountType.INSTALLATION)
                .name("account")
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(LocationOnShoreDTO.builder().build())
                .legalEntity(LegalEntityDTO.builder().id(1L).build()).build();
        
        AccountDTO persistedAccountDTO = AccountDTO.builder()
                .id(accountId)
                .accountType(AccountType.INSTALLATION)
                .name("account")
                .competentAuthority(CompetentAuthority.ENGLAND)
                .location(LocationOnShoreDTO.builder().build())
                .legalEntity(LegalEntityDTO.builder().id(1L).name("le").build()).build();
        
        RequestParams requestParams = RequestParams.builder()
                .type(INSTALLATION_ACCOUNT_OPENING)
                .ca(accountPayload.getCompetentAuthority())
                .accountId(accountId)
                .requestPayload(requestPayload)
                .build();
        
        Request request = Request.builder().competentAuthority(CompetentAuthority.ENGLAND).creationDate(LocalDateTime.now()).build();
        
        when(accountCreationService.createAccount(accountDTO, pmrvUser)).thenReturn(persistedAccountDTO);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);
        
        //invoke
        handler.process(null, RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION, createActionPayload, pmrvUser);
        
        assertThat(request.getSubmissionDate()).isEqualTo(request.getCreationDate());
        verify(accountCreationService, times(1)).createAccount(accountDTO, pmrvUser);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
        verify(requestService, times(1))
                .addActionToRequest(request,
                    accountSubmittedPayload,
                    RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED,
                    pmrvUser.getUserId());
        

        ArgumentCaptor<EmailData> recipientEmailInformationCaptor = ArgumentCaptor.forClass(EmailData.class);
        verify(notificationEmailService, times(1)).notifyRecipient(recipientEmailInformationCaptor.capture(), Mockito.eq(pmrvUser.getEmail()));
        EmailData emailData = recipientEmailInformationCaptor.getValue();
        assertThat(emailData.getNotificationTemplateData().getTemplateName()).isEqualTo(ACCOUNT_APPLICATION_RECEIVED);
        assertThat(emailData.getNotificationTemplateData().getTemplateParams()).isEmpty();
        
    }

}
