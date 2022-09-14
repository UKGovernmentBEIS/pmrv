package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

@Service
@RequiredArgsConstructor
public class RequestActionUserInfoResolver {

    private final UserAuthService userAuthService;
    private final AccountContactQueryService accountContactQueryService;
    private final OperatorAuthorityQueryService operatorAuthorityQueryService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;

    @Transactional(readOnly = true)
    public String getUserFullName(final String userId) {
        return userAuthService.getUserByUserId(userId).getFullName();
    }

    @Transactional(readOnly = true)
    public Map<String, RequestActionUserInfo> getUsersInfo(final Set<String> operators, final String signatory, final Request request) {
        final UserInfoDTO requestAccountPrimaryContactUserInfo =
            requestAccountContactQueryService.getRequestAccountPrimaryContact(request);

        final Set<String> userIds = Stream.of(operators, Set.of(signatory, requestAccountPrimaryContactUserInfo.getUserId()))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

        return getUsersInfo(userIds, request.getAccountId());
    }

    private Map<String, RequestActionUserInfo> getUsersInfo(final Set<String> userIds,
                                                                    final Long accountId) {
        final Map<AccountContactType, String> contactTypes =
            accountContactQueryService.findContactTypesByAccount(accountId);
        final List<AuthorityRoleDTO> authorities =
            operatorAuthorityQueryService.findOperatorUserAuthoritiesListByAccount(accountId);

        return userIds.stream().collect(
            Collectors.toMap(id -> id, id -> this.createUserInfo(id, contactTypes, authorities))
        );
    }

    private RequestActionUserInfo createUserInfo(final String userId,
                                                 final Map<AccountContactType, String> contacts,
                                                 final List<AuthorityRoleDTO> authorities) {
        final Set<AccountContactType> contactTypes = contacts.entrySet().stream()
                .filter(e -> e.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        final String role = authorities
            .stream()
            .filter(a -> a.getUserId().equals(userId))
            .findFirst()
            .map(AuthorityRoleDTO::getRoleCode)
            .orElse(null);

        return RequestActionUserInfo.builder()
            .name(getUserFullName(userId))
            .roleCode(role)
            .contactTypes(contactTypes)
            .build();
    }
}
