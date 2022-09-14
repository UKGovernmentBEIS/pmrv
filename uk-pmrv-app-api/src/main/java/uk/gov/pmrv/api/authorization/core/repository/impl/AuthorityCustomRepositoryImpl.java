package uk.gov.pmrv.api.authorization.core.repository.impl;

import org.springframework.stereotype.Repository;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityCustomRepository;
import uk.gov.pmrv.api.authorization.operator.domain.OperatorUserAssignedSubResource;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserAssignedSubResource;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierUserAssignedSubResource;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Authority custom repository implementation
 *
 */
@Repository
public class AuthorityCustomRepositoryImpl implements AuthorityCustomRepository {
    private static final String USER_ID = "userId";
    private static final String RESOURCE_TYPE = "resourceType";
    private static final String SCOPE = "scope";
    private static final String ACCOUNT_IDS = "accountIds";

    @PersistenceContext
	private EntityManager em;

    @Override
    public Map<Long, Set<String>> findResourceSubTypesOperatorUserHasScopeByAccounts(String userId, Set<Long> accounts,
                                                                                     ResourceType resourceType, Scope scope) {
        return em.createNamedQuery(Authority.NAMED_QUERY_FIND_OPERATOR_USER_ASSIGNED_RESOURCE_SUB_TYPES_BY_ACCOUNT, OperatorUserAssignedSubResource.class)
            .setParameter(USER_ID, userId)
            .setParameter(ACCOUNT_IDS, accounts)
            .setParameter(RESOURCE_TYPE, resourceType)
            .setParameter(SCOPE, scope)
            .getResultStream()
            .collect(Collectors.groupingBy(OperatorUserAssignedSubResource::getAccountId,
                Collectors.mapping(OperatorUserAssignedSubResource::getResourceSubType, Collectors.toSet())));
    }

    @Override
    public Map<CompetentAuthority, Set<String>> findResourceSubTypesRegulatorUserHasScope(String userId, ResourceType resourceType, Scope scope){
        return
          em.createNamedQuery(Authority.NAMED_QUERY_FIND_REGULATOR_USER_ASSIGNED_RESOURCE_SUB_TYPES, RegulatorUserAssignedSubResource.class)
            .setParameter(USER_ID, userId)
            .setParameter(RESOURCE_TYPE, resourceType)
            .setParameter(SCOPE, scope)
            .getResultStream()
            .collect(
                    Collectors.groupingBy(RegulatorUserAssignedSubResource::getCa,
                            Collectors.mapping(RegulatorUserAssignedSubResource::getResourceSubType, Collectors.toSet())));
    }

    @Override
    public Map<Long, Set<String>> findResourceSubTypesVerifierUserHasScope(String userId, ResourceType resourceType, Scope scope){
        return em.createNamedQuery(Authority.NAMED_QUERY_FIND_VERIFIER_USER_ASSIGNED_RESOURCE_SUB_TYPES, VerifierUserAssignedSubResource.class)
                .setParameter(USER_ID, userId)
                .setParameter(RESOURCE_TYPE, resourceType)
                .setParameter(SCOPE, scope)
                .getResultStream()
                .collect(Collectors.groupingBy(VerifierUserAssignedSubResource::getVerificationBodyId,
                        Collectors.mapping(VerifierUserAssignedSubResource::getResourceSubType, Collectors.toSet())));
    }
	
	@Override
	public List<String> findOperatorUsersByAccountId(Long accountId){
	    return em.createNamedQuery(Authority.NAMED_QUERY_FIND_OPERATOR_USERS_BY_ACCOUNT, String.class)
                .setParameter("accountId", accountId)
                .getResultStream()
                .collect(Collectors.toList());
	}
	
	@Override
    public List<String> findRegulatorUsersByCompetentAuthority(CompetentAuthority competentAuthority){
        return em.createNamedQuery(Authority.NAMED_QUERY_FIND_REGULATOR_USERS_BY_CA, String.class)
                .setParameter("competentAuthority", competentAuthority)
                .getResultStream()
                .collect(Collectors.toList());
	}
        
    @Override
    public List<String> findVerifierUsersByVerificationBodyId(Long verificationBodyId){
        return em.createNamedQuery(Authority.NAMED_QUERY_FIND_VERIFIER_USERS_BY_VB_ID, String.class)
                .setParameter("verificationBodyId", verificationBodyId)
                .getResultStream()
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, AuthorityStatus> findStatusByUsersAndAccountId(List<String> userIds, Long accountId) {
        return em.createNamedQuery(Authority.NAMED_QUERY_FIND_STATUS_BY_USERS_AND_ACCOUNT_ID, Tuple.class)
                .setParameter("userIds", userIds)
                .setParameter("accountId", accountId)
                .getResultStream()
                .collect(
                        Collectors.toMap(
                                t-> (String)t.get(USER_ID),
                                t-> (AuthorityStatus)t.get("status")));
    }

    @Override
    public Map<String, AuthorityStatus> findStatusByUsersAndCA(final List<String> userIds,
                                                               final CompetentAuthority competentAuthority) {
        return em.createNamedQuery(Authority.NAMED_QUERY_FIND_STATUS_BY_USERS_AND_CA, Tuple.class)
            .setParameter("userIds", userIds)
            .setParameter("competentAuthority", competentAuthority)
            .getResultStream()
            .collect(
                Collectors.toMap(
                    t -> (String) t.get(USER_ID),
                    t -> (AuthorityStatus) t.get("status")));
    }
}
