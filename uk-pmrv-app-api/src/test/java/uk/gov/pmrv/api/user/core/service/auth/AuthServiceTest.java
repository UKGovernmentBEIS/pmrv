package uk.gov.pmrv.api.user.core.service.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.PENDING;
import static uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus.REGISTERED;
import static uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes.USER_STATUS;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.JwtTokenActionEnum;
import uk.gov.pmrv.api.user.core.config.JwtProperties;
import uk.gov.pmrv.api.user.core.config.JwtProperties.Claim;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.domain.model.core.SignatureRequest;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInfoDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String REALM = "realm";

    @InjectMocks
    private AuthService authService;

    @Mock
    private Keycloak keycloakAdminClient;

    @Mock
    private KeycloakCustomClient keycloakCustomClient;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private KeycloakSpringBootProperties keycloakProperties;

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RealmResource realmResource;

    @Mock
    private UsersResource usersResource;

    @Mock
    private Response response;

    @Mock
    private UserResource userResource;

    @Spy
    private final Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    @Test
    void getUserRepresentationById() {
        String userId = "user";

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);

        authService.getUserRepresentationById(userId);

        verify(keycloakProperties, times(1)).getRealm();
        verify(keycloakAdminClient, times(1)).realm(REALM);
        verify(realmResource, times(1)).users();
        verify(usersResource, times(1)).get(userId);
        verify(userResource, times(1)).toRepresentation();
    }

    @Test
    void getUsers() {
        List<String> userIds = List.of("user1", "user2");
        List<UserInfo> userInfos = List.of(
            UserInfo.builder().id("user1").build(),
            UserInfo.builder().id("user2").build()
        );
        when(keycloakCustomClient.getUsers(userIds)).thenReturn(userInfos);

        List<UserInfo> result = authService.getUsers(userIds);

        verify(keycloakCustomClient, times(1)).getUsers(userIds);
        assertThat(result)
            .extracting(UserInfo::getId)
            .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void getUsersWithAttributes() {
        List<String> userIds = List.of("id1", "id2");
        List<RegulatorUserInfoDTO> expectedUsers =
            List.of(RegulatorUserInfoDTO.builder().id("id1").build(),
                RegulatorUserInfoDTO.builder().id("id2").build());

        when(keycloakCustomClient.getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class))
            .thenReturn(expectedUsers);

        List<RegulatorUserInfoDTO> result = authService.getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class);

        verify(keycloakCustomClient, times(1)).getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class);
        assertThat(result)
            .extracting(RegulatorUserInfoDTO::getId)
            .containsExactlyInAnyOrder("id1", "id2");
    }

    @Test
    void getMiReportUsersWithAttributes() {
        List<String> userIds = List.of("id1", "id2");
        List<OperatorUserInfoDTO> expectedUsers = IntStream.range(1, 3)
            .mapToObj(i -> OperatorUserInfoDTO.builder()
                .firstName("name" + i)
                .lastLoginDate(DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()))
                .build()
            )
            .collect(Collectors.toList());

        when(keycloakCustomClient.getUsersWithAttributes(userIds, OperatorUserInfoDTO.class))
            .thenReturn(expectedUsers);

        List<OperatorUserInfoDTO> result = authService.getUsersWithAttributes(userIds, OperatorUserInfoDTO.class);

        verify(keycloakCustomClient, times(1)).getUsersWithAttributes(userIds, OperatorUserInfoDTO.class);
        assertThat(result)
            .extracting(OperatorUserInfoDTO::getFirstName)
            .containsExactlyInAnyOrder("name1", "name2");
    }

    @Test
    void getUserDetails() {
        String userId = "userId";
        UserDetails userDetails = UserDetails.builder()
            .id(userId)
            .signature(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("sign").build())
            .build();

        when(keycloakCustomClient.getUserDetails(userId)).thenReturn(Optional.of(userDetails));

        Optional<UserDetails> result = authService.getUserDetails(userId);

        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(userDetails);
        verify(keycloakCustomClient, times(1)).getUserDetails(userId);
    }

    @Test
    void getUserSignature() {
        String signatureUuid = UUID.randomUUID().toString();
        FileDTO signature = FileDTO.builder()
            .fileContent("content".getBytes())
            .fileName("signature")
            .fileSize(1L)
            .fileType("type")
            .build();

        when(keycloakCustomClient.getUserSignature(signatureUuid)).thenReturn(Optional.of(signature));

        Optional<FileDTO> result = authService.getUserSignature(signatureUuid);

        assertThat(result).isNotEmpty();
        assertThat(result.get()).isEqualTo(signature);
        verify(keycloakCustomClient, times(1)).getUserSignature(signatureUuid);
    }

    @Test
    void updateUserTerms() {
        String userId = "user";
        Short currentTermsVersion = 1;
        Short newTermsVersion = 2;

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.singleAttribute(KeycloakUserAttributes.TERMS_VERSION.getName(),
            String.valueOf(currentTermsVersion));

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0))
            .isEqualTo(String.valueOf(currentTermsVersion));

        authService.updateUserTerms(userId, newTermsVersion);

        verify(keycloakProperties, times(2)).getRealm();
        verify(keycloakAdminClient, times(2)).realm(REALM);
        verify(realmResource, times(2)).users();
        verify(usersResource, times(2)).get(userId);
        verify(userResource, times(1)).toRepresentation();
        verify(userResource, times(1)).update(userRepresentation);

        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0))
            .isEqualTo(String.valueOf(newTermsVersion));
    }

    @Test
    void updateUser() {
        String userId = "user";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);

        authService.updateUser(userRepresentation);

        verify(keycloakProperties, times(1)).getRealm();
        verify(keycloakAdminClient, times(1)).realm(REALM);
        verify(realmResource, times(1)).users();
        verify(usersResource, times(1)).get(userId);
        verify(userResource, times(1)).update(userRepresentation);
    }

    @Test
    void updateUserDetails() throws UserDetailsSaveException {
        UserDetailsRequest userDetails = UserDetailsRequest.builder()
            .id("userId")
            .signature(
                SignatureRequest.builder().content("content".getBytes()).name("name").size(1L).type("type").build())
            .build();

        authService.updateUserDetails(userDetails);

        verify(keycloakCustomClient, times(1)).saveUserDetails(userDetails);
    }

    @Test
    void updateUserDetails_throws_UserDetailsSaveException() {
        UserDetailsRequest userDetails = UserDetailsRequest.builder()
            .id("userId")
            .signature(
                SignatureRequest.builder().content("content".getBytes()).name("name").size(1L).type("type").build())
            .build();

        doThrow(new RuntimeException("saving error")).when(keycloakCustomClient).saveUserDetails(userDetails);

        try {
            authService.updateUserDetails(userDetails);
            fail("should not reach here");
        } catch (UserDetailsSaveException e) {
            assertThat(e.getCause().getMessage()).isEqualTo("saving error");
        } catch (Exception e) {
            fail("should not throw different exception");
        }

        verify(keycloakCustomClient, times(1)).saveUserDetails(userDetails);
    }

    @Test
    void registerUser() {
        final String userId = "userId";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);

        //mock
        mockRegisterUser_created(userRepresentation, userId);

        //invoke
        String actualUserId = authService.registerUser(userRepresentation);

        //verify mocks
        verify(keycloakProperties, times(1)).getRealm();
        verify(keycloakAdminClient, times(1)).realm(REALM);
        verify(realmResource, times(1)).users();
        verify(usersResource, times(1)).create(userRepresentation);
        verify(response, times(1)).getStatus();

        assertThat(actualUserId).isEqualTo(userId);
    }

    @Test
    void registerUserFailed() {
        String userId = "userId";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);

        //mock
        mockRegisterUser_failed(userRepresentation);

        //invoke
        assertThrows(BusinessException.class, () -> authService.registerUser(userRepresentation));

        //verify mocks
        verify(keycloakProperties, times(1)).getRealm();
        verify(keycloakAdminClient, times(1)).realm(REALM);
        verify(realmResource, times(1)).users();
        verify(usersResource, times(1)).create(userRepresentation);
        verify(response, times(1)).getStatus();
    }

    @Test
    void generateToken() {
        String EMAIL = "email@email";
        Claim claim = mock(Claim.class);
        JwtTokenActionEnum tokenAction = JwtTokenActionEnum.USER_REGISTRATION;

        //mock
        when(keycloakProperties.getCredentials().get("secret")).thenReturn("secret");
        when(jwtProperties.getClaim()).thenReturn(claim);
        when(claim.getAudience()).thenReturn("jwtAudience");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        //invoke
        authService.generateToken(tokenAction.getSubject(), tokenAction.getClaimName(), EMAIL, 5L);

        //verify mocks
        verify(jwtProperties, times(1)).getClaim();
        verify(claim, times(1)).getAudience();
    }

    @Test
    void getByUsername() {
        final String id = "userId";
        final String userName = "userName";
        final String email = "email@email";
        final UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(id);
        userRepresentation.setUsername(userName);
        userRepresentation.setEmail(email);

        //mock
        mockFindByEmail(List.of(userRepresentation), email);

        //invoke
        Optional<UserRepresentation> result = authService.getByUsername(email);

        //assert
        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());

        //verify mocks
        findByEmailVerifications(email);
    }

    @Test
    void getByUsername_empty() {
        String email = "email@email";
        //mock
        mockFindByEmail(Collections.emptyList(), email);

        //invoke
        Optional<UserRepresentation> result = authService.getByUsername(email);

        //assert
        assertFalse(result.isPresent());

        //verify mocks
        findByEmailVerifications(email);
    }

    @Test
    void verifyToken() {
        final JwtTokenActionEnum tokenAction = JwtTokenActionEnum.USER_REGISTRATION;
        when(keycloakProperties.getCredentials().get("secret")).thenReturn("secret");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        // Mock token
        ZonedDateTime now = ZonedDateTime.now(fixedClock);
        String token = JWT.create()
            .withIssuer("authServerUrl")
            .withIssuedAt(Date.from(now.toInstant()))
            .withSubject(tokenAction.getSubject())
            .withAudience("jwtAudience")
            .withExpiresAt(
                Date.from(ZonedDateTime.now(fixedClock).plusMinutes(1000L).toInstant()))
            .sign(Algorithm.HMAC256("secret"));

        DecodedJWT decodedJwt = authService.verifyToken(token, tokenAction.getSubject());

        assertEquals(tokenAction.getSubject(), decodedJwt.getSubject());
    }

    @Test
    void verifyTokenExpired() {
        final String tokenSubject = "email@email";

        //mock
        when(keycloakProperties.getCredentials().get("secret")).thenReturn("secret");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        String token = JWT.create()
            .withIssuer("authServerUrl")
            .withIssuedAt(
                Date.from(ZonedDateTime.now(fixedClock).minusDays(10).toInstant()))
            .withSubject(tokenSubject)
            .withAudience("jwtAudience")
            .withExpiresAt(
                Date.from(ZonedDateTime.now(fixedClock).minusDays(5).toInstant()))
            .sign(Algorithm.HMAC256("secret"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.verifyToken(token, tokenSubject);
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_LINK_EXPIRED);
    }

    @Test
    void verifyTokenInvalid() {
        final JwtTokenActionEnum tokenAction = JwtTokenActionEnum.USER_REGISTRATION;

        //mock
        when(keycloakProperties.getCredentials().get("secret")).thenReturn("secret");
        when(keycloakProperties.getAuthServerUrl()).thenReturn("authServerUrl");

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.verifyToken("xxxx", tokenAction.getSubject());
        });

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    void disableUser() {
        final String userId = "user";
        final String realm = "realm";
        final UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.setEnabled(true);
        when(keycloakProperties.getRealm()).thenReturn(realm);
        when(keycloakAdminClient.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        final AuthenticationStatus newAuthenticationStatus = AuthenticationStatus.DELETED;
        //invoke
        authService.disableUser(userId);

        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);

        //verify
        verify(keycloakProperties, times(2)).getRealm();
        verify(keycloakAdminClient, times(2)).realm(realm);
        verify(realmResource, times(2)).users();
        verify(usersResource, times(2)).get(userId);
        verify(userResource, times(1)).toRepresentation();
        verify(userResource, times(1)).update(userCaptor.capture());
        UserRepresentation updateUser = userCaptor.getValue();

        //assert
        assertThat(updateUser.getAttributes()).isNotEmpty();
        assertThat(updateUser.isEnabled()).isFalse();
        assertThat(updateUser.getAttributes().get(USER_STATUS.getName())).containsExactly(
            newAuthenticationStatus.name());
    }

    @Test
    void enablePendingUser_pending_user() {
        String userId = "user";
        String password = "password";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.singleAttribute(USER_STATUS.getName(), PENDING.name());

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        authService.enablePendingUser(userId, password);

        UserRepresentation result = authService.getUserRepresentationById(userId);
        assertThat(result.getAttributes().get(USER_STATUS.getName()).get(0)).isEqualTo(REGISTERED.name());
        assertThat(result.getCredentials().get(0).getValue()).isEqualTo(password);
        assertTrue(result.isEnabled());

    }

    @Test
    void enablePendingUser_not_pending_user() {
        String userId = "user";
        String password = "password";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.singleAttribute(USER_STATUS.getName(), REGISTERED.name());

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        BusinessException businessException =
            assertThrows(BusinessException.class, () -> authService.enablePendingUser(userId, password));

        assertEquals(ErrorCode.USER_INVALID_STATUS, businessException.getErrorCode());
    }

    @Test
    void registerUserWithStatusPending() {
        final String userId = "userId";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);

        //mock
        mockRegisterUser_created(userRepresentation, userId);

        //invoke
        authService.registerUserWithStatusPending(userRepresentation);

        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(usersResource, times(1)).create(userCaptor.capture());
        UserRepresentation registeredUser = userCaptor.getValue();

        assertThat(registeredUser.getId()).isEqualTo(userId);
        assertThat(registeredUser.getAttributes().get(USER_STATUS.getName()))
            .containsOnly(PENDING.name());
        assertFalse(registeredUser.isEnabled());

        verify(keycloakProperties, times(1)).getRealm();
        verify(keycloakAdminClient, times(1)).realm(REALM);
        verify(realmResource, times(1)).users();
        verify(response, times(1)).getStatus();
    }

    @Test
    void enableRegisteredUser() {
        String userId = "user";
        String password = "password";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.singleAttribute(USER_STATUS.getName(), REGISTERED.name());

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        authService.enableRegisteredUser(userId, password);

        UserRepresentation result = authService.getUserRepresentationById(userId);
        assertThat(result.getAttributes().get(USER_STATUS.getName()).get(0)).isEqualTo(
            AuthenticationStatus.REGISTERED.name());
        assertThat(result.getCredentials().get(0).getValue()).isEqualTo(password);
        assertTrue(result.isEnabled());
    }

    @Test
    void enableRegisteredUser_user_not_registered() {
        String userId = "user";
        String password = "password";

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userId);
        userRepresentation.singleAttribute(USER_STATUS.getName(), PENDING.name());

        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        BusinessException businessException =
            assertThrows(BusinessException.class, () -> authService.enableRegisteredUser(userId, password));

        assertEquals(ErrorCode.USER_INVALID_STATUS, businessException.getErrorCode());
    }

    @Test
    void updateUserAndSetStatusAsPending() {
        final String userId = "userId";
        final String realm = "realm";
        final String firstName = "firstName";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setFirstName(firstName);

        when(keycloakProperties.getRealm()).thenReturn(realm);
        when(keycloakAdminClient.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);

        //invoke
        authService.updateUserAndSetStatusAsPending(userId, userRepresentation);

        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);

        //verify
        verify(keycloakProperties, times(1)).getRealm();
        verify(keycloakAdminClient, times(1)).realm(realm);
        verify(realmResource, times(1)).users();
        verify(usersResource, times(1)).get(userId);
        verify(userResource, times(1)).update(userCaptor.capture());
        UserRepresentation updateUser = userCaptor.getValue();

        //assert
        assertThat(updateUser.getAttributes()).isNotEmpty();
        assertThat(updateUser.isEnabled()).isFalse();
        assertThat(updateUser.getAttributes().get(USER_STATUS.getName())).containsExactly(PENDING.name());
        assertThat(updateUser.getFirstName()).isEqualTo(firstName);
    }

    @Test
    void validateAuthenticatedUserOtp() {
        String otp = "otp";
        String accessToken = "accessToken";

        authService.validateAuthenticatedUserOtp(otp, accessToken);

        verify(keycloakCustomClient, times(1)).validateAuthenticatedUserOtp(otp, accessToken);
    }

    @Test
    void deleteOtpCredentials() {
        final String userId = "userId";
        final String realm = "realm";
        final String credentialId = "id";
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType("otp");
        credentialRepresentation.setId(credentialId);

        // Mock
        when(keycloakProperties.getRealm()).thenReturn(realm);
        when(keycloakAdminClient.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(userId)).thenReturn(userResource);
        when(usersResource.get(userId).credentials()).thenReturn(List.of(credentialRepresentation));

        // Invoke
        authService.deleteOtpCredentials(userId);

        // Verify
        verify(usersResource.get(userId), times(1)).removeCredential(credentialId);
    }

    private void mockRegisterUser_created(UserRepresentation userRepresentation, String userIdCreated) {
        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(userRepresentation)).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.CREATED.value());
        when(response.getStatusInfo()).thenReturn(Response.Status.CREATED);
        when(response.getLocation()).thenReturn(URI.create("http://www.pmrv.uk/" + userIdCreated));
    }

    private void mockRegisterUser_failed(UserRepresentation userRepresentation) {
        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(userRepresentation)).thenReturn(response);
        when(response.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void mockFindByEmail(List<UserRepresentation> userRepresentations, String email) {
        when(keycloakProperties.getRealm()).thenReturn(REALM);
        when(keycloakAdminClient.realm(REALM)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.search(email, true))
            .thenReturn(userRepresentations);
    }

    private void findByEmailVerifications(String email) {
        verify(keycloakProperties, times(1)).getRealm();
        verify(keycloakAdminClient, times(1)).realm(REALM);
        verify(realmResource, times(1)).users();
        verify(usersResource, times(1)).search(email, true);
    }

}
