package uk.gov.pmrv.api.authorization.rules.services.authorization;

import java.util.function.BiPredicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class PmrvAuthorizationService {

    private final PmrvResourceAuthorizationServiceDelegator resourceAuthorizationServiceDelegator;

    /**
     * Authorizes user based on {@link AuthorizationCriteria} and {@link RoleType}.
     * @param user the authenticated user
     * @param authorizationCriteria the {@link AuthorizationCriteria} based on which criteria the authorization is performed on.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     */
    public void authorize(PmrvUser user, AuthorizationCriteria authorizationCriteria) {
        boolean isAuthorized = resourceAuthorizationServiceDelegator
            .isAuthorized(getResourceType(user, authorizationCriteria), user, authorizationCriteria);

        if (!isAuthorized) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private ResourceType getResourceType(PmrvUser user, AuthorizationCriteria authorizationCriteria) {
        ResourceType resourceType = null;
        if (checkAccountAccess().test(user, authorizationCriteria)) {
            resourceType = ResourceType.ACCOUNT;
        } else if (checkCompetentAuthorityAccess().test(user, authorizationCriteria)) {
            resourceType = ResourceType.CA;
        } else if (checkVerificationBodyAccess().test(user, authorizationCriteria)) {
            resourceType = ResourceType.VERIFICATION_BODY;
        }

        return resourceType;
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> checkAccountAccess() {
        return accountOnlyCriteria().or(operatorUserRole());
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> checkCompetentAuthorityAccess() {
        return competentAuthorityOnlyCriteria().or(regulatorUserRole());
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> checkVerificationBodyAccess() {
        return verificationBodyOnlyCriteria().or(verifierUserRole());
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> accountOnlyCriteria() {
        return (user, criteria) -> ObjectUtils.isNotEmpty(criteria.getAccountId())
            && criteria.getCompetentAuthority() == null
            && ObjectUtils.isEmpty(criteria.getVerificationBodyId());
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> competentAuthorityOnlyCriteria() {
        return (user, criteria) -> ObjectUtils.isNotEmpty(criteria.getAccountId())
            && criteria.getCompetentAuthority() != null
            && ObjectUtils.isEmpty(criteria.getVerificationBodyId());
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> verificationBodyOnlyCriteria() {
        return (user, criteria) -> ObjectUtils.isEmpty(criteria.getAccountId())
            && criteria.getCompetentAuthority() == null
            && ObjectUtils.isNotEmpty(criteria.getVerificationBodyId());
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> operatorUserRole() {
        return (user, criteria) -> user.getRoleType() == RoleType.OPERATOR;
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> regulatorUserRole() {
        return (user, criteria) -> user.getRoleType() == RoleType.REGULATOR;
    }

    private BiPredicate<PmrvUser, AuthorizationCriteria> verifierUserRole() {
        return (user, criteria) -> user.getRoleType() == RoleType.VERIFIER;
    }
}
