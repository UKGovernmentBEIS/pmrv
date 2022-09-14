package uk.gov.pmrv.api.authorization.operator.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.transform.AuthorityMapper;
import uk.gov.pmrv.api.authorization.core.transform.UserAuthorityMapper;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@Service
@RequiredArgsConstructor
public class OperatorAuthorityQueryService {

	private final AuthorityRepository authorityRepository;
	private final AccountAuthorizationResourceService accountAuthorizationResourceService;
    private final UserAuthorityMapper userAuthorityMapper = Mappers.getMapper(UserAuthorityMapper.class);
    private final AuthorityMapper authorityMapper;

    public UserAuthoritiesDTO getAccountAuthorities(PmrvUser authUser, Long accountId) {

        boolean hasAuthUserEditUserScopeOnAccount =
            accountAuthorizationResourceService.hasUserScopeToAccount(authUser, accountId, Scope.EDIT_USER);

        List<AuthorityRoleDTO> operatorUserAuthorities = hasAuthUserEditUserScopeOnAccount ?
            findOperatorUserAuthoritiesListByAccount(accountId) :
            findNonPendingOperatorUserAuthoritiesListByAccount(accountId);

        List<UserAuthorityDTO> accountAuthorities = operatorUserAuthorities.stream()
            .map(authorityRole -> userAuthorityMapper.toUserAuthority(authorityRole, true))
            .collect(Collectors.toList());

        return UserAuthoritiesDTO.builder()
                .authorities(accountAuthorities)
                .editable(hasAuthUserEditUserScopeOnAccount)
                .build();
    }

    public List<AuthorityRoleDTO> findOperatorUserAuthoritiesListByAccount(Long accountId) {
        return authorityRepository.findOperatorUserAuthorityRoleListByAccount(accountId);
    }

    public List<AuthorityInfoDTO> findByAccountIds(List<Long> accountIds) {
        return authorityRepository.findByAccountIdIn(accountIds).stream().map(authorityMapper::toAuthorityInfoDTO).collect(Collectors.toList());
    }

    private List<AuthorityRoleDTO> findNonPendingOperatorUserAuthoritiesListByAccount(Long accountId) {
        return authorityRepository.findNonPendingOperatorUserAuthorityRoleListByAccount(accountId);
    }
}
