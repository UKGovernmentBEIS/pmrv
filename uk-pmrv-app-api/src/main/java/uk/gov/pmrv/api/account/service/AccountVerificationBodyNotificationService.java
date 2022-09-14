package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType;
import uk.gov.pmrv.api.notification.message.service.SystemMessageNotificationService;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AccountVerificationBodyNotificationService {
    
    private final SystemMessageNotificationService systemMessageNotificationService;
    private final VerifierAuthorityQueryService verifierAuthorityQueryService;
    private final OperatorAuthorityService operatorAuthorityService;

    public void notifyUsersForVerificationBodyApppointment(Long verificationBodyId, Account account) {
        List<String> verifierAdmins = verifierAuthorityQueryService.findVerifierAdminsByVerificationBody(verificationBodyId);
        verifierAdmins
            .forEach(ver -> systemMessageNotificationService.generateAndSendNotificationSystemMessage(
                    createNewVerificationBodyInstallationSystemMessage(verificationBodyId, account, ver)));
    }
    
    public void notifyUsersForVerificationBodyUnapppointment(Set<Account> accountsUnappointed) {
        accountsUnappointed
            .forEach(acc -> {
                List<String> operatorAdmins = operatorAuthorityService.findActiveOperatorAdminUsersByAccount(acc.getId());
                operatorAdmins.forEach(op ->
                        systemMessageNotificationService.generateAndSendNotificationSystemMessage(
                            createVerifierNoLongerAvailableSystemMessage(acc, op)));
            });
    }
    
    private SystemMessageNotificationInfo createNewVerificationBodyInstallationSystemMessage(
        Long verificationBodyId, Account account, String verifierAdmin) {
        return SystemMessageNotificationInfo.builder()
                .messageType(SystemMessageNotificationType.NEW_VERIFICATION_BODY_INSTALLATION)
                .messageParameters(Map.of(
                        "operatorName", account.getLegalEntity().getName(),
                        "accountName", account.getName()))
                .accountId(account.getId())
                .competentAuthority(account.getCompetentAuthority())
                .verificationBodyId(verificationBodyId)
                .receiver(verifierAdmin)
                .build();
    }
    
    private SystemMessageNotificationInfo createVerifierNoLongerAvailableSystemMessage(
            Account account, String operatorAdmin) {
        return SystemMessageNotificationInfo.builder()
                .messageType(SystemMessageNotificationType.VERIFIER_NO_LONGER_AVAILABLE)
                .messageParameters(Map.of("accountId", account.getId()))
                .accountId(account.getId())
                .competentAuthority(account.getCompetentAuthority())
                .receiver(operatorAdmin)
                .build();
    }
}
