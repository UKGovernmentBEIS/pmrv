package uk.gov.pmrv.api.user.operator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
class OperatorUserTokenVerificationServiceTest {
	
	@InjectMocks
	private OperatorUserTokenVerificationService service;
	
	@Mock
	private UserAuthService userAuthService;
	
	@Test
	void verifyRegistrationToken() {
		String token = "token";
		String userEmail = "email";
		
    	when(userAuthService.resolveTokenActionClaim(token, JwtTokenActionEnum.USER_REGISTRATION))
    		.thenReturn(userEmail);
    	when(userAuthService.getUserByEmail(userEmail)).thenReturn(Optional.empty());
    	
    	String result = service.verifyRegistrationToken(token);
    	
    	assertThat(result).isEqualTo(userEmail);
    	
    	verify(userAuthService, times(1)).getUserByEmail(userEmail);
	}
	
	@Test
	void verifyRegistrationToken_user_exists() {
		String token = "token";
		String userEmail = "email";
		
		UserInfoDTO user = 
				UserInfoDTO.builder()
    				.build();
		
    	when(userAuthService.resolveTokenActionClaim(token, JwtTokenActionEnum.USER_REGISTRATION))
    		.thenReturn(userEmail);
    	when(userAuthService.getUserByEmail(userEmail)).thenReturn(Optional.of(user));
    	
    	//invoke
    	BusinessException ex = assertThrows(BusinessException.class, () -> {
    		service.verifyRegistrationToken(token);
    	});
    	assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.USER_ALREADY_REGISTERED);

    	verify(userAuthService, times(1)).getUserByEmail(userEmail);
	}
}
