package uk.gov.pmrv.api.authorization.rules.services;

import java.util.Arrays;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

/**
 * Service that authorizes user based only on {@link RoleType}.
 */
@Service
public class RoleAuthorizationService {

    /**
     * Evaluates whether the {@code pmrvUser} has any of the {@code roleTypes}.
     *
     * @param pmrvUser {@link PmrvUser}
     * @param roleTypes the {@link RoleType} array
     */
    public void evaluate(PmrvUser pmrvUser, RoleType[] roleTypes) {
        if (!Arrays.asList(roleTypes).contains(pmrvUser.getRoleType()) || pmrvUser.getAuthorities().isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }
}
