package uk.gov.pmrv.api.user.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@ExtendWith(MockitoExtension.class)
class UserFileTokenServiceTest {

    @InjectMocks
    private UserFileTokenService service;

    @Mock
    private UserAuthService userAuthService;
    
    @Mock
    private JwtProperties jwtProperties;
    
    @Test
    void generateGetFileToken() {
        String attachmentUuid = "uuid";
        String token = "token";
        long tokenExpirationMinutes = 1l;
        FileToken fileToken = FileToken.builder()
                .token(token)
                .tokenExpirationMinutes(tokenExpirationMinutes)
                .build();
        
        uk.gov.pmrv.api.user.core.config.JwtProperties.Claim claim = Mockito.mock(uk.gov.pmrv.api.user.core.config.JwtProperties.Claim.class);
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getGetFileAttachmentExpIntervalMinutes()).thenReturn(tokenExpirationMinutes);
        when(userAuthService.generateToken(
                JwtTokenActionEnum.GET_FILE,
                attachmentUuid, 
                tokenExpirationMinutes)).thenReturn(token);
        
        FileToken result = service.generateGetFileToken(attachmentUuid);
        
        assertThat(result).isEqualTo(fileToken);
        verify(jwtProperties, times(1)).getClaim();
        verify(claim, times(1)).getGetFileAttachmentExpIntervalMinutes();
        verify(userAuthService, times(1)).generateToken(
                JwtTokenActionEnum.GET_FILE,
                attachmentUuid, 
                tokenExpirationMinutes);
    }
    
    @Test
    void resolveGetFileAttachmentUuid() {
        String getFileAttachmentToken = "token";
        String attachmentUuid = "resolvedAttachmentUuid";
        when(userAuthService.resolveTokenActionClaim(getFileAttachmentToken, JwtTokenActionEnum.GET_FILE))
            .thenReturn(attachmentUuid);
        
        String result = service.resolveGetFileUuid(getFileAttachmentToken);
        
        assertThat(result).isEqualTo(attachmentUuid);
        verify(userAuthService, times(1)).resolveTokenActionClaim(getFileAttachmentToken, JwtTokenActionEnum.GET_FILE);
    }
    
}
