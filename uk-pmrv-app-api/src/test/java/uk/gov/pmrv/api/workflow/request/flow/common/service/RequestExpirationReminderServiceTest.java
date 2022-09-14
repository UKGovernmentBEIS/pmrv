package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;

@ExtendWith(MockitoExtension.class)
class RequestExpirationReminderServiceTest {

    @InjectMocks
    private RequestExpirationReminderService service;

    @Mock
    private RequestService requestService;
    
    @Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private PermitQueryService permitQueryService;
    
    @Mock
    private NotificationEmailService notificationEmailService;
    
    @Test
    void sendExpirationReminderNotification() {
        String requestId = "1";
        Long accountId = 1L;
        Date deadline = new Date();
        NotificationTemplateExpirationReminderParams expirationParams = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask("request for information")
                .recipient(UserInfoDTO.builder()
                        .email("recipient@email")
                        .firstName("fn").lastName("ln")
                        .build())
                .expirationTime("1 day")
                .expirationTimeLong("in one day")
                .deadline(deadline)
                .build();
        
        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE)
                .competentAuthority(CompetentAuthority.ENGLAND)
                .build();
        String permitId = "permitId";
        AccountDTO account = AccountDTO.builder()
                .id(accountId)
                .name("account name")
                .legalEntity(LegalEntityDTO.builder().name("le name").build())
                .build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(permitQueryService.getPermitIdByAccountId(accountId)).thenReturn(Optional.of(permitId));
        when(accountQueryService.getAccountDTOById(accountId)).thenReturn(account);
        
        //invoke
        service.sendExpirationReminderNotification(requestId, expirationParams);
        
        verify(requestService, times(1)).findRequestById(requestId);
        verify(permitQueryService, times(1)).getPermitIdByAccountId(accountId);
        verify(accountQueryService, times(1)).getAccountDTOById(accountId);

        ArgumentCaptor<EmailData> emailDataCaptor = ArgumentCaptor.forClass(EmailData.class);
        
        verify(notificationEmailService, times(1)).notifyRecipient(emailDataCaptor.capture(), Mockito.eq("recipient@email"));
        EmailData emailDataCaptured = emailDataCaptor.getValue();
        
        final Map<String, Object> expectedTemplateParams = new HashMap<>();
        expectedTemplateParams.put(EmailNotificationTemplateConstants.ACCOUNT_LEGAL_ENTITY_NAME, account.getLegalEntity().getName());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.ACCOUNT_NAME, account.getName());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.PERMIT_ID, permitId);
        expectedTemplateParams.put(EmailNotificationTemplateConstants.WORKFLOW_ID, request.getId());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.WORKFLOW, request.getType().getDescription());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.WORKFLOW_TASK, expirationParams.getWorkflowTask());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.WORKFLOW_USER, expirationParams.getRecipient().getFullName());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME, expirationParams.getExpirationTime());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.WORKFLOW_EXPIRATION_TIME_LONG, expirationParams.getExpirationTimeLong());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.WORKFLOW_DEADLINE, expirationParams.getDeadline());
        expectedTemplateParams.put(EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL, request.getCompetentAuthority().getEmail());
        
        assertThat(emailDataCaptured).isEqualTo(EmailData.builder()
                .notificationTemplateData(EmailNotificationTemplateData.builder()
                        .competentAuthority(CompetentAuthority.ENGLAND)
                        .templateName(NotificationTemplateName.GENERIC_EXPIRATION_REMINDER)
                        .templateParams(expectedTemplateParams)
                        .build())
                .build());
        
    }
    
}
