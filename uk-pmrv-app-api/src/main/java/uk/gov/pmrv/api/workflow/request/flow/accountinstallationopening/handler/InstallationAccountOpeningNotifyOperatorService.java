package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.ACCOUNT_APPLICATION_REJECTED_REASON;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_FNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.APPLICANT_LNAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_EMAIL;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_NAME;
import static uk.gov.pmrv.api.notification.mail.constants.EmailNotificationTemplateConstants.COMPETENT_AUTHORITY_PHONE;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.ACCOUNT_APPLICATION_ACCEPTED;
import static uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName.ACCOUNT_APPLICATION_REJECTED;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.notification.mail.domain.EmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.mail.domain.EmailData;
import uk.gov.pmrv.api.notification.mail.service.NotificationEmailService;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.Decision;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountOpeningDecisionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

/**
 * Handler for the Notify Operator service task of the installation account opening BPMN process.
 */
@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningNotifyOperatorService {

    private final NotificationEmailService notificationEmailService;
    private final UserService userService;
    private final RequestService requestService;

    public void execute(String requestId) {
        Request request = requestService.findRequestById(requestId);
        sendAccountApplicationUpdateEmail(request);
    }

    private void sendAccountApplicationUpdateEmail(Request request) {
        AccountOpeningDecisionPayload accountOpeningDecisionPayload = ((InstallationAccountOpeningRequestPayload) request.getPayload()).getAccountOpeningDecisionPayload();
        boolean isApplicationAccepted = accountOpeningDecisionPayload.getDecision() == Decision.ACCEPTED;
        final String userId = request.getPayload().getOperatorAssignee();
        ApplicationUserDTO applicantUserDTO = userService.getUserById(userId);

        EmailData emailData = EmailData.builder()
            .notificationTemplateData(EmailNotificationTemplateData.builder()
                    .templateName(isApplicationAccepted ? ACCOUNT_APPLICATION_ACCEPTED : ACCOUNT_APPLICATION_REJECTED)
                    .templateParams(buildTemplateParams(isApplicationAccepted, applicantUserDTO, request.getCompetentAuthority(), accountOpeningDecisionPayload))
                    .build())
            .build();
        notificationEmailService.notifyRecipient(emailData, applicantUserDTO.getEmail());
    }

    private Map<String, Object> buildTemplateParams(boolean isApplicationAccepted, ApplicationUserDTO userDTO,
                                                        CompetentAuthority competentAuthority, AccountOpeningDecisionPayload accountOpeningDecisionPayload) {
        Map<String, Object> dataModelParams = new HashMap<>();
        dataModelParams.put(COMPETENT_AUTHORITY_NAME, competentAuthority.getName());
        dataModelParams.put(COMPETENT_AUTHORITY_EMAIL, competentAuthority.getEmail());
        dataModelParams.put(COMPETENT_AUTHORITY_PHONE, competentAuthority.getPhone());
        dataModelParams.put(APPLICANT_FNAME, userDTO.getFirstName());
        dataModelParams.put(APPLICANT_LNAME, userDTO.getLastName());

        if (!isApplicationAccepted) {
            dataModelParams.put(ACCOUNT_APPLICATION_REJECTED_REASON, accountOpeningDecisionPayload.getReason());
        }
        return dataModelParams;
    }
}
