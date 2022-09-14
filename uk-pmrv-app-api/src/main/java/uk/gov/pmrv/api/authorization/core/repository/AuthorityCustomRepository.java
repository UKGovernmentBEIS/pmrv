package uk.gov.pmrv.api.authorization.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

public interface AuthorityCustomRepository {

    @Transactional(readOnly = true)
    Map<Long, Set<String>> findResourceSubTypesOperatorUserHasScopeByAccounts(String userId, Set<Long> accounts,
                                                                              ResourceType resourceType, Scope scope);

	@Transactional(readOnly = true)
    Map<CompetentAuthority, Set<String>> findResourceSubTypesRegulatorUserHasScope(String userId, ResourceType resourceType, Scope scope);

    @Transactional(readOnly = true)
    Map<Long, Set<String>> findResourceSubTypesVerifierUserHasScope(String userId, ResourceType resourceType, Scope scope);
	
	@Transactional(readOnly = true)
    List<String> findOperatorUsersByAccountId(Long accountId);
	
	@Transactional(readOnly = true)
    List<String> findRegulatorUsersByCompetentAuthority(CompetentAuthority competentAuthority);
	
	@Transactional(readOnly = true)
    List<String> findVerifierUsersByVerificationBodyId(Long verificationBodyId);
	
	@Transactional(readOnly = true)
    Map<String, AuthorityStatus> findStatusByUsersAndAccountId(List<String> userIds, Long accountId);
    
    @Transactional(readOnly = true)
    Map<String, AuthorityStatus> findStatusByUsersAndCA(List<String> userIds, CompetentAuthority competentAuthority);
}
