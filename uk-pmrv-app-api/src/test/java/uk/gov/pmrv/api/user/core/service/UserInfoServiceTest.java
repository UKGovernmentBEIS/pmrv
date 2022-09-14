package uk.gov.pmrv.api.user.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @InjectMocks
    private UserInfoService userInfoService;

    @Mock
    private UserAuthService userAuthService;

    @Test
    void getUsersInfo_with_edit_user_authority() {
        String userId = "userId";
        String fn = "fn";
        String ln = "ln";
        boolean hasAuthorityToEditUsers = true;

        UserInfo userInfo = UserInfo.builder().id(userId).firstName(fn).lastName(ln).enabled(true).build();
        List<UserInfoDTO> expectedUserInfoList = List.of(
            UserInfoDTO.builder().userId(userId).firstName(fn).lastName(ln).locked(false).build()
        );

        when(userAuthService.getUsers(List.of(userId))).thenReturn(List.of(userInfo));

        List<UserInfoDTO> actualUserInfoList = userInfoService
            .getUsersInfo(List.of(userId), hasAuthorityToEditUsers);

        assertThat(actualUserInfoList).containsExactlyElementsOf(expectedUserInfoList);

        verify(userAuthService, times(1)).getUsers(List.of(userId));
    }

    @Test
    void getUsersInfo_without_edit_user_authority() {
        String userId = "userId";
        String fn = "fn";
        String ln = "ln";
        boolean hasAuthorityToEditUsers = false;

        UserInfo userInfo = UserInfo.builder().id(userId).firstName(fn).lastName(ln).enabled(true).build();
        List<UserInfoDTO> expectedUserInfoList = List.of(
            UserInfoDTO.builder().userId(userId).firstName(fn).lastName(ln).build()
        );

        when(userAuthService.getUsers(List.of(userId))).thenReturn(List.of(userInfo));

        List<UserInfoDTO> actualUserInfoList = userInfoService
            .getUsersInfo(List.of(userId), hasAuthorityToEditUsers);

        assertThat(actualUserInfoList).containsExactlyElementsOf(expectedUserInfoList);

        verify(userAuthService, times(1)).getUsers(List.of(userId));
    }
}