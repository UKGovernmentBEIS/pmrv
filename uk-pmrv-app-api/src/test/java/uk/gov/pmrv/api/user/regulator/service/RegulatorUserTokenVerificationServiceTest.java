package uk.gov.pmrv.api.user.regulator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.service.UserInvitationTokenVerificationService;

@ExtendWith(MockitoExtension.class)
class RegulatorUserTokenVerificationServiceTest {

    @InjectMocks
    private RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;

    @Mock
    private UserInvitationTokenVerificationService userInvitationTokenVerificationService;

    @Test
    void verifyInvitationToken() {
        String invitationToken = "invitationToken";
        JwtTokenActionEnum tokenAction = JwtTokenActionEnum.REGULATOR_INVITATION;
        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .userId("user")
            .authorityStatus(AuthorityStatus.PENDING)
            .accountId(1L)
            .build();

        when(userInvitationTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken, tokenAction))
            .thenReturn(authorityInfo);

        AuthorityInfoDTO actual = regulatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken);

        assertEquals(authorityInfo, actual);

    }

}