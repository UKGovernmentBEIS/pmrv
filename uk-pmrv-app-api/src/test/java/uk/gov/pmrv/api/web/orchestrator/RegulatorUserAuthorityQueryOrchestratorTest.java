package uk.gov.pmrv.api.web.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInfoService;
import uk.gov.pmrv.api.web.orchestrator.dto.RegulatorUserAuthorityInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.RegulatorUsersAuthoritiesInfoDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class RegulatorUserAuthorityQueryOrchestratorTest {

    @InjectMocks
    private RegulatorUserAuthorityQueryOrchestrator regulatorUserAuthorityQueryOrchestrator;

    @Mock
    private RegulatorAuthorityQueryService regulatorAuthorityQueryService;

    @Mock
    private RegulatorUserInfoService regulatorUserInfoService;

    @Test
    void getCaRegulators() {
        String userId = "userId";
        AuthorityStatus status = ACTIVE;
        PmrvUser authUser = PmrvUser.builder()
                .userId("authUserId")
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
                .build();
        UserAuthorityDTO userAuthority = UserAuthorityDTO.builder()
                .userId(userId)
                .authorityStatus(status)
                .build();
        UserAuthoritiesDTO userAuthorities = UserAuthoritiesDTO.builder()
                .authorities(List.of(userAuthority))
                .editable(true)
                .build();
        RegulatorUserInfoDTO userInfo = RegulatorUserInfoDTO.builder().id(userId).enabled(true).build();

        RegulatorUserAuthorityInfoDTO expectedUserAuthInfo =
                RegulatorUserAuthorityInfoDTO.builder().userId(userId).authorityStatus(status).locked(false).build();

        when(regulatorAuthorityQueryService.getCaAuthorities(authUser)).thenReturn(userAuthorities);
        when(regulatorUserInfoService.getRegulatorUsersInfo(authUser, List.of(userId))).thenReturn(List.of(userInfo));

        RegulatorUsersAuthoritiesInfoDTO caRegulators = regulatorUserAuthorityQueryOrchestrator.getCaUsersAuthoritiesInfo(authUser);

        assertTrue(caRegulators.isEditable());
        assertThat(caRegulators.getCaUsers()).hasSize(1);
        assertEquals(expectedUserAuthInfo, caRegulators.getCaUsers().get(0));

        verify(regulatorAuthorityQueryService, times(1)).getCaAuthorities(authUser);
        verify(regulatorUserInfoService, times(1)).getRegulatorUsersInfo(authUser, List.of(userId));
    }
}