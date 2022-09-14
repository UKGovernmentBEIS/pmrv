package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.service.auth.UserRegistrationService;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.transform.VerifierUserMapper;

@Service
@RequiredArgsConstructor
public class VerifierUserAuthService {

    private final AuthService authService;
    private final UserRegistrationService userRegistrationService;
    private final VerifierUserMapper verifierUserMapper;

    public VerifierUserDTO getVerifierUserById(String userId) {
        return verifierUserMapper.toVerifierUserDTO(authService.getUserRepresentationById(userId));
    }

    public void updateVerifierUser(String userId, VerifierUserDTO verifierUserDTO) {
        UserRepresentation registeredUser = authService.getUserRepresentationById(userId);
        UserRepresentation updatedUser = verifierUserMapper.toUserRepresentation(verifierUserDTO, userId,
                registeredUser.getUsername(), registeredUser.getEmail(), registeredUser.getAttributes());

        authService.updateUser(updatedUser);
    }

    /**
     * Registers the invited verifier user in the system. If the user already exists in the system, the information
     * contained in the {@code verifierUserInvitation} is used to update user's info.
     * @param verifierUserInvitation the {@link VerifierUserInvitationDTO} containing invited user's info.
     * @return the unique id for the user
     */
    @Transactional
    public String registerInvitedVerifierUser(VerifierUserInvitationDTO verifierUserInvitation) {
        UserRepresentation newUserRepresentation = verifierUserMapper.toUserRepresentation(verifierUserInvitation);
        return userRegistrationService.registerInvitedUser(newUserRepresentation);
    }
}
