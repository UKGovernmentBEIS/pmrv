package uk.gov.pmrv.api.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAuthService;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserAuthService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserAuthService;

/**
 * The User Service.
 */
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRoleTypeService userRoleTypeService;
    private final OperatorUserAuthService operatorUserAuthService;
    private final RegulatorUserAuthService regulatorUserAuthService;
    private final VerifierUserAuthService verifierUserAuthService;

    public ApplicationUserDTO getUserById(String userId) {
    	UserRoleTypeDTO userRoleTypeDTO = userRoleTypeService.getUserRoleTypeByUserId(userId);

        switch (userRoleTypeDTO.getRoleType()) {
            case OPERATOR:
                return operatorUserAuthService.getOperatorUserById(userId);
            case REGULATOR:
                return regulatorUserAuthService.getRegulatorUserById(userId);
            case VERIFIER:
                return verifierUserAuthService.getVerifierUserById(userId);
            default:
            	throw new UnsupportedOperationException(String.format("Unsupported role type %s", userRoleTypeDTO.getRoleType()));
        }
    }
}
