package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType.ACCOUNT_USERS_SETUP;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.message.service.SystemMessageNotificationService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class InstallationAccountMessageAccountUsersSetupService {
    private final SystemMessageNotificationService systemMessageNotificationService;
    private final RequestService requestService;

    public void execute(String requestId) {

        Request request = requestService.findRequestById(requestId);
        Long accountId = request.getAccountId();

        SystemMessageNotificationInfo msgInfo = SystemMessageNotificationInfo.builder()
            .messageType(ACCOUNT_USERS_SETUP)
            .messageParameters(Map.of("accountId", accountId))
            .accountId(accountId)
            .competentAuthority(request.getCompetentAuthority())
            .receiver(request.getPayload().getOperatorAssignee())
            .build();

        systemMessageNotificationService.generateAndSendNotificationSystemMessage(msgInfo);
    }
}
