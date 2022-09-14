package uk.gov.pmrv.api.common.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.AccessToken;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@ExtendWith(MockitoExtension.class)
class PmrvUserMapperTest {

    private final PmrvUserMapperImpl pmrvUserMapper = new PmrvUserMapperImpl();

    @Test
    void toPmrvUser() {
        AuthorityDTO accountAuthority = buildAccountAuthority();
        AuthorityDTO caAuthority = buildCaAuthority();
        AccessToken accessToken = buildAccessToken();
        RoleType roleType = RoleType.OPERATOR;

        PmrvUser expectedPmrvUser = getExpectedPmrvUser(accountAuthority, caAuthority, accessToken, roleType);

        PmrvUser pmrvUser = pmrvUserMapper.toPmrvUser(accessToken, List.of(accountAuthority, caAuthority), roleType);

        assertEquals(expectedPmrvUser, pmrvUser);
    }

    @Test
    void toPmrvUser_no_authorities() {
        AccessToken accessToken = buildAccessToken();
        RoleType roleType = RoleType.OPERATOR;

        PmrvUser expectedPmrvUser = PmrvUser.builder()
            .userId(accessToken.getSubject())
            .firstName(accessToken.getGivenName())
            .lastName(accessToken.getFamilyName())
            .email(accessToken.getEmail())
            .roleType(roleType)
            .build();

        PmrvUser pmrvUser = pmrvUserMapper.toPmrvUser(accessToken, Collections.emptyList(), roleType);

        assertEquals(expectedPmrvUser, pmrvUser);
    }

    private PmrvUser getExpectedPmrvUser(AuthorityDTO accountAuthority, AuthorityDTO caAuthority, AccessToken accessToken, RoleType roleType) {
        return PmrvUser.builder()
                .userId(accessToken.getSubject())
                .firstName(accessToken.getGivenName())
                .lastName(accessToken.getFamilyName())
                .email(accessToken.getEmail())
                .roleType(roleType)
                .authorities(List.of(
                        PmrvAuthority.builder()
                                .code(accountAuthority.getCode())
                                .accountId(accountAuthority.getAccountId())
                                .permissions(accountAuthority.getAuthorityPermissions())
                                .build(),
                        PmrvAuthority.builder()
                                .code(caAuthority.getCode())
                                .competentAuthority(caAuthority.getCompetentAuthority())
                                .permissions(caAuthority.getAuthorityPermissions())
                                .build()))
                .build();
    }

    private AuthorityDTO buildCaAuthority() {
        return AuthorityDTO.builder()
                .code("code2")
                .competentAuthority(CompetentAuthority.SCOTLAND)
                .authorityPermissions(List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
                        PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK))
                .build();
    }

    private AuthorityDTO buildAccountAuthority() {
        return AuthorityDTO.builder()
                .code("code1")
                .accountId(1L)
                .authorityPermissions(List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK,
                        PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK))
                .build();
    }

    private AccessToken buildAccessToken() {
        Map<String, String> token = Map.of("sub", "userId", "email", "user@email.com", "given_name", "name", "family_name", "surname");
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(token, AccessToken.class);
    }
}