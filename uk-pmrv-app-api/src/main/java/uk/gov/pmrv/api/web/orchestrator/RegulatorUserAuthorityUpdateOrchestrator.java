package uk.gov.pmrv.api.web.orchestrator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityUpdateService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserUpdateDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserManagementService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserNotificationGateway;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegulatorUserAuthorityUpdateOrchestrator {

    private final RegulatorUserManagementService regulatorUserManagementService;
    private final RegulatorAuthorityUpdateService regulatorAuthorityUpdateService;
    private final RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Transactional
    public void updateRegulatorUserByUserId(PmrvUser pmrvUser, String userId,
                                            RegulatorUserUpdateDTO regulatorUserUpdateDTO, FileDTO signature) {
        // Update authority permissions
        regulatorAuthorityUpdateService.updateRegulatorUserPermissions(pmrvUser, userId, regulatorUserUpdateDTO.getPermissions());
        // Update regulator user details in keycloak
        regulatorUserManagementService.updateRegulatorUserByUserId(pmrvUser, userId, regulatorUserUpdateDTO.getUser(), signature);
    }

    public void updateRegulatorUsersStatus(final List<RegulatorUserUpdateStatusDTO> regulatorUsers,
                                           final PmrvUser authUser) {

        final List<String> activatedRegulators =
                regulatorAuthorityUpdateService.updateRegulatorUsersStatus(regulatorUsers, authUser);

        if(!activatedRegulators.isEmpty()) {
            regulatorUserNotificationGateway.sendUpdateNotifications(activatedRegulators);
        }
    }

}
