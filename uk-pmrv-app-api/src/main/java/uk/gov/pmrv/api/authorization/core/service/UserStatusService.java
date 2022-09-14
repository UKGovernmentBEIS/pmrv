package uk.gov.pmrv.api.authorization.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus.ACCEPTED;
import static uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus.DELETED;
import static uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus.DISABLED;
import static uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus.ENABLED;
import static uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus.NO_AUTHORITY;
import static uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus.TEMP_DISABLED;

@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final AuthorityRepository authorityRepository;
    private final UserAuthService userAuthService;

    public LoginStatus getLoginStatus(String userId) {

        List<Authority> userAuthorities = authorityRepository.findByUserId(userId);

        if (!userAuthorities.isEmpty()) {
            List<Authority> activeUserAuthorities = getActiveUserAuthorities(userAuthorities);

            if(!activeUserAuthorities.isEmpty()) {
                List<Authority> activeUserAuthoritiesWithPermissions = getActiveUserAuthoritiesWithPermissions(activeUserAuthorities);

                //if user has only active authorities that do not have permissions assigned then return NO_AUTHORITY
                if(activeUserAuthoritiesWithPermissions.isEmpty()) {
                    return NO_AUTHORITY;
                }

                return ENABLED;

            } else {
                //if user has authorities, but none active
                if(userAuthorities.stream()
                        .anyMatch(ua -> ua.getStatus().equals(AuthorityStatus.ACCEPTED))){
                    return ACCEPTED;
                }

                return userAuthorities.stream().anyMatch(ua -> ua.getStatus().equals(AuthorityStatus.TEMP_DISABLED))
                        ? TEMP_DISABLED : DISABLED;
            }
        }

        //if user has no authorities at all
        return userAuthService.getUserByUserId(userId).getStatus().equals(AuthenticationStatus.DELETED) ?
            DELETED : NO_AUTHORITY;

    }

    private List<Authority> getActiveUserAuthorities(List<Authority> userAuthorities) {
        return userAuthorities.stream()
            .filter(au -> au.getStatus().equals(ACTIVE)).collect(Collectors.toList());
    }

    private List<Authority> getActiveUserAuthoritiesWithPermissions(List<Authority> activeUserAuthorities) {
        return activeUserAuthorities.stream()
            .filter(au -> !au.getAuthorityPermissions().isEmpty())
            .collect(Collectors.toList());
    }
}
