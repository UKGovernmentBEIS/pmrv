package uk.gov.pmrv.api.user.operator.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.UserInfoService;

@Service
@RequiredArgsConstructor
public class OperatorUserInfoService {

    private final AccountAuthorizationResourceService accountAuthorizationResourceService;
    private final UserInfoService userInfoService;

    public List<UserInfoDTO> getOperatorUsersInfo(PmrvUser authUser, Long accountId, List<String> userIds) {
        boolean hasAuthUserEditUserScopeOnAccount =
            accountAuthorizationResourceService.hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER);

        return userInfoService.getUsersInfo(userIds, hasAuthUserEditUserScopeOnAccount);
    }
}
