package uk.gov.pmrv.api.user.core.service.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.transform.UserMapper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final AuthService authService;
    private final UserMapper userMapper;

    public UserInfoDTO getUserByUserId(String userId) {
        return userMapper.toUserInfoDTO(authService.getUserRepresentationById(userId));
    }

    public Optional<UserInfoDTO> getUserByEmail(String email) {
        return authService
                .getByUsername(email)
                .map(userMapper::toUserInfoDTO);
    }
    
    public List<UserInfo> getUsers(List<String> userIds) {
        return authService.getUsers(userIds);
    }
    
    public <T> List<T> getUsersWithAttributes(List<String> userIds, Class<T> attributesClazz) {
        return authService.getUsersWithAttributes(userIds, attributesClazz);
    }
    
    public Optional<UserDetails> getUserDetails(String userId) {
        return authService.getUserDetails(userId);
    }
    
    public Optional<FileDTO> getUserSignature(String signatureUuid) {
        return authService.getUserSignature(signatureUuid);
    }

    public void enablePendingUser(String userId, String password) {
        authService.enablePendingUser(userId, password);
    }
    
    public void updateUserTerms(String userId, Short newTermsVersion) {
        authService.updateUserTerms(userId, newTermsVersion);
    }

    public String generateToken(JwtTokenActionEnum jwtTokenAction, String claimValue, long expirationInterval) {
        return authService.generateToken(jwtTokenAction.getSubject(), jwtTokenAction.getClaimName(), claimValue, expirationInterval);
    }

    public String resolveTokenActionClaim(String token, JwtTokenActionEnum jwtTokenAction) {
        DecodedJWT decodedJwt = authService.verifyToken(token, jwtTokenAction.getSubject());
        return decodedJwt.getClaim(jwtTokenAction.getClaimName()).asString();
    }

    public void validateAuthenticatedUserOtp(String otp, String token) {
        authService.validateAuthenticatedUserOtp(otp, token);
    }

    public void deleteOtpCredentials(String email) {
        authService.getByUsername(email)
                .ifPresentOrElse(userRepresentation -> authService.deleteOtpCredentials(userRepresentation.getId()),
                        () -> {throw new BusinessException(ErrorCode.USER_NOT_EXIST);});
    }
}
