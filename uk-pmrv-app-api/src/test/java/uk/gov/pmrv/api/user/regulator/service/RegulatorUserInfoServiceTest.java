package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorUserInfoServiceTest {
    @InjectMocks
    private RegulatorUserInfoService regulatorUserInfoService;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Mock
    private UserAuthService userAuthService;

    @Test
    void getRegulatorUsersInfo() {
        PmrvUser pmrvUser = PmrvUser.builder()
                .userId("authUserId")
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
                .build();
        RegulatorUserInfoDTO regulatorUserInfoDTO = RegulatorUserInfoDTO.builder()
                .id("user1")
                .firstName("name")
                .lastName("lastName")
                .jobTitle("title")
                .enabled(true)
                .build();
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(pmrvUser, CompetentAuthority.ENGLAND, Scope.EDIT_USER)).thenReturn(true);
        when(userAuthService.getUsersWithAttributes(List.of("user1"), RegulatorUserInfoDTO.class)).thenReturn(List.of(regulatorUserInfoDTO));
        List<RegulatorUserInfoDTO> regulatorUsersInfo = regulatorUserInfoService.getRegulatorUsersInfo(pmrvUser, List.of("user1"));
        assertEquals(regulatorUsersInfo, List.of(regulatorUserInfoDTO));
    }

    @Test
    void getRegulatorUsersInfo_no_edit() {
        PmrvUser pmrvUser = PmrvUser.builder()
                .userId("authUserId")
                .authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()))
                .build();
        RegulatorUserInfoDTO regulatorUserInfoDTO = RegulatorUserInfoDTO.builder()
                .id("user1")
                .firstName("name")
                .lastName("lastName")
                .jobTitle("title")
                .enabled(null)
                .build();
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(pmrvUser, CompetentAuthority.ENGLAND, Scope.EDIT_USER)).thenReturn(false);
        when(userAuthService.getUsersWithAttributes(List.of("user1"), RegulatorUserInfoDTO.class)).thenReturn(List.of(regulatorUserInfoDTO));
        List<RegulatorUserInfoDTO> regulatorUsersInfo = regulatorUserInfoService.getRegulatorUsersInfo(pmrvUser, List.of("user1"));
        assertEquals(regulatorUsersInfo, List.of(regulatorUserInfoDTO));
    }
}