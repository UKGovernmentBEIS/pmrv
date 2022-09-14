package uk.gov.pmrv.api.user.operator.service;

import static uk.gov.pmrv.api.authorization.AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class OperatorUserInvitationService {

    private final UserAuthService authUserService;
    private final UserRoleTypeService userRoleTypeService;
    private final OperatorUserRegistrationService operatorUserRegistrationService;
    private final ExistingOperatorUserInvitationService existingOperatorUserInvitationService;

    /**
     * Invites a new user to join an account with a specified role.
     * @param accountId the account id
     * @param userRegistrationDTO the {@link OperatorUserInvitationDTO}
     * @param currentUser the current logged-in {@link PmrvUser}
     */
    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void inviteUserToAccount(Long accountId, OperatorUserInvitationDTO userRegistrationDTO, PmrvUser currentUser) {
        validateRequesterUserCapabilityToAddOthersToAccount(currentUser, userRegistrationDTO.getRoleCode());

        Optional<UserInfoDTO> userOptional = authUserService.getUserByEmail(userRegistrationDTO.getEmail());

        userOptional.ifPresentOrElse(
            userRepresentation -> addExistingUserToAccount(userRepresentation, userRegistrationDTO, accountId, currentUser),
            () -> operatorUserRegistrationService.registerUserToAccountWithStatusPending(userRegistrationDTO, accountId, currentUser));
    }

    private void validateRequesterUserCapabilityToAddOthersToAccount(PmrvUser currentUser, String roleCode) {
        // Regulator user can only add operator administrator users to an account
        if (userRoleTypeService.isUserRegulator(currentUser.getUserId()) && !OPERATOR_ADMIN_ROLE_CODE.equalsIgnoreCase(roleCode)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_REGULATOR_NOT_ALLOWED_TO_ADD_OPERATOR_ROLE_TO_ACCOUNT);
        }
    }

    private void addExistingUserToAccount(UserInfoDTO userDTO, OperatorUserInvitationDTO userRegistrationDTO,
                                          Long accountId, PmrvUser currentUser) {
        existingOperatorUserInvitationService.addExistingUserToAccount(userRegistrationDTO, accountId, userDTO.getUserId(), userDTO.getStatus(), currentUser);
    }

}
