package uk.gov.pmrv.api.user.regulator.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.service.UserSignatureValidatorService;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.service.auth.UserDetailsSaveException;
import uk.gov.pmrv.api.user.core.service.auth.UserRegistrationService;
import uk.gov.pmrv.api.user.core.transform.UserDetailsMapper;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.transform.RegulatorInviteUserMapper;
import uk.gov.pmrv.api.user.regulator.transform.RegulatorUserMapper;

@Service
@RequiredArgsConstructor
public class RegulatorUserAuthService {

	private final AuthService authService;
    private final UserRegistrationService userRegistrationService;
    private final UserSignatureValidatorService userSignatureValidatorService;
    
    private final RegulatorUserMapper regulatorUserMapper;
    private final RegulatorInviteUserMapper regulatorInviteUserMapper;
    private final UserDetailsMapper userDetailsMapper;
    
    public RegulatorUserDTO getRegulatorUserById(String userId) {
        UserRepresentation userRep = authService.getUserRepresentationById(userId);
        return regulatorUserMapper.toRegulatorUserDTO(
                userRep, 
                authService.getUserDetails(userId).map(UserDetails::getSignature).orElse(null)
                );
    }
    
    public String registerRegulatorInvitedUser(RegulatorInvitedUserDetailsDTO regulatorUserInvitation, FileDTO signature) {
        userSignatureValidatorService.validateSignature(signature);
        
        UserRepresentation newUserRepresentation = regulatorInviteUserMapper.toUserRepresentation(regulatorUserInvitation);
        String userId = userRegistrationService.registerInvitedUser(newUserRepresentation);

        try {
            authService.updateUserDetails(
                    userDetailsMapper.toUserDetails(userId, signature));
        } catch (UserDetailsSaveException e) {
            //rollback register
            authService.deleteUser(userId);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e);
        }
        
        return userId;
    }
    
    public void updateRegulatorUser(String userId, RegulatorUserDTO newRegulatorUserDTO, FileDTO signature) {
        userSignatureValidatorService.validateSignature(signature);
        
        UserRepresentation registeredUser = authService.getUserRepresentationById(userId);
        
        UserRepresentation updatedUser = regulatorUserMapper.toUserRepresentation(newRegulatorUserDTO, userId,
                registeredUser.getUsername(), registeredUser.getEmail(), registeredUser.getAttributes());
        authService.updateUser(updatedUser);
        
        try {
            authService.updateUserDetails(
                    userDetailsMapper.toUserDetails(userId, signature));
        } catch (UserDetailsSaveException e) {
            //rollback update
            authService.updateUser(registeredUser);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e);
        }
    }
    
}
