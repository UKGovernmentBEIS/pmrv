package uk.gov.pmrv.api.web.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityUpdateService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserNotificationGateway;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;

@ExtendWith(MockitoExtension.class)
class RegulatorUserAuthorityUpdateOrchestratorTest {

    @InjectMocks
    private RegulatorUserAuthorityUpdateOrchestrator orchestrator;

    @Mock
    private RegulatorAuthorityUpdateService regulatorAuthorityUpdateService;
    
    @Mock
    private RegulatorUserNotificationGateway regulatorUserNotificationGateway;

    @Test
    void updateRegulatorUsersStatus_WhenNoExceptions_thenFlowCompletes() {

        final RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus1 =
            RegulatorUserUpdateStatusDTO.builder().userId("user1").authorityStatus(AuthorityStatus.ACTIVE).build();
        final RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus2 =
            RegulatorUserUpdateStatusDTO.builder().userId("user2").authorityStatus(AuthorityStatus.ACTIVE).build();
        final List<RegulatorUserUpdateStatusDTO>
            regulatorUsers = List.of(regulatorUserUpdateStatus1, regulatorUserUpdateStatus2);
        final PmrvUser authUser = buildRegulatorUser("regUserId", ENGLAND);

        when(regulatorAuthorityUpdateService.updateRegulatorUsersStatus(regulatorUsers, authUser))
            .thenReturn(List.of("user1"));

        orchestrator.updateRegulatorUsersStatus(regulatorUsers, authUser);

        verify(regulatorAuthorityUpdateService, times(1)).updateRegulatorUsersStatus(regulatorUsers, authUser);
        verify(regulatorUserNotificationGateway, times(1)).sendUpdateNotifications(List.of("user1"));
    }

    private PmrvUser buildRegulatorUser(final String userId, final CompetentAuthority ca) {
        return PmrvUser.builder()
            .userId(userId)
            .roleType(RoleType.REGULATOR)
            .authorities(
                List.of(PmrvAuthority.builder()
                    .competentAuthority(ca)
                    .build()
                )
            )
            .build();
    }
}