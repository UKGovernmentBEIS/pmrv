package uk.gov.pmrv.api.user.core.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class UserFileTokenService {

    private final UserAuthService userAuthService;
    private final JwtProperties jwtProperties;
    
    public String resolveGetFileUuid(String getFileToken) {
        return userAuthService.resolveTokenActionClaim(getFileToken, JwtTokenActionEnum.GET_FILE);
    }

    public FileToken generateGetFileToken(String fileUuid) {
        long expirationMinutes = jwtProperties.getClaim().getGetFileAttachmentExpIntervalMinutes();
        String token = userAuthService.generateToken(JwtTokenActionEnum.GET_FILE,
                fileUuid,
                expirationMinutes);
        return FileToken.builder().token(token).tokenExpirationMinutes(expirationMinutes).build();
    }
}
