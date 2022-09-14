package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegulatorUserInfoService {

    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final UserAuthService userAuthService;

    public List<RegulatorUserInfoDTO> getRegulatorUsersInfo(PmrvUser pmrvUser, List<String> userIds) {
        boolean hasEditUserScopeOnCa = compAuthAuthorizationResourceService.hasUserScopeToCompAuth(pmrvUser, pmrvUser.getCompetentAuthority(), Scope.EDIT_USER);

        List<RegulatorUserInfoDTO> regulatorsUserInfo = userAuthService.getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class);
        if(!hasEditUserScopeOnCa) {
            for (RegulatorUserInfoDTO regulatorUserInfo: regulatorsUserInfo) {
                regulatorUserInfo.setEnabled(null);
            }
        }
        return regulatorsUserInfo;
    }
}
