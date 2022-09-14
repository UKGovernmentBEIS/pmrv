package uk.gov.pmrv.api.user.verifier.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class VerifierUserMapperTest {

    private VerifierUserMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(VerifierUserMapper.class);
    }

    @Test
    void toVerifierUserDTO() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.USER_STATUS.getName(), List.of(AuthenticationStatus.REGISTERED.name()));
            put(KeycloakUserAttributes.TERMS_VERSION.getName(), List.of("1"));
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
            put(KeycloakUserAttributes.MOBILE_NUMBER.getName(), List.of("2101313132"));
        }});

        // Invoke
        VerifierUserDTO verifierUserDTO = mapper.toVerifierUserDTO(userRepresentation);

        // Assert
        assertEquals(userRepresentation.getEmail(), verifierUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), verifierUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), verifierUserDTO.getLastName());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0), verifierUserDTO.getStatus().name());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0), verifierUserDTO.getTermsVersion().toString());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), verifierUserDTO.getPhoneNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), verifierUserDTO.getMobileNumber());
    }

    @Test
    void toUserRepresentation() {
        String userId = "userId";
        String username = "username";
        String status = AuthenticationStatus.REGISTERED.name();
        String terms = "1";
        Map<String, List<String>> attributes = Map.of(KeycloakUserAttributes.USER_STATUS.getName(), List.of(status),
                KeycloakUserAttributes.TERMS_VERSION.getName(), List.of(terms));

        VerifierUserDTO verifierUserDTO = VerifierUserDTO.builder()
                .email("fromUI")
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("2101313131")
                .mobileNumber("2101313132")
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(verifierUserDTO, userId, username, username, attributes);

        // Assert
        assertEquals(userId, userRepresentation.getId());
        assertEquals(username, userRepresentation.getUsername());
        assertEquals(username, userRepresentation.getEmail());
        assertEquals(verifierUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(verifierUserDTO.getLastName(), userRepresentation.getLastName());
        assertEquals(status, userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0));
        assertEquals(terms, userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0));
        assertEquals(verifierUserDTO.getPhoneNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(verifierUserDTO.getMobileNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toUserRepresentation_UserInvitation() {
        String username = "username";
        VerifierUserInvitationDTO verifierUserInvitationDTO = VerifierUserInvitationDTO.builder()
                .roleCode("roleCode")
                .email(username)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("2101313131")
                .mobileNumber("2101313132")
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(verifierUserInvitationDTO);

        // Assert
        assertEquals(username, userRepresentation.getUsername());
        assertEquals(username, userRepresentation.getEmail());
        assertEquals(verifierUserInvitationDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(verifierUserInvitationDTO.getLastName(), userRepresentation.getLastName());
        assertEquals(verifierUserInvitationDTO.getPhoneNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(verifierUserInvitationDTO.getMobileNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toVerifierUserInvitationDTO() {
        AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO = AdminVerifierUserInvitationDTO.builder()
            .email("email")
            .firstName("firstName")
            .lastName("lastName")
            .phoneNumber("2101313131")
            .mobileNumber("2101313132")
            .build();

        VerifierUserInvitationDTO verifierUserInvitationDTO =
            mapper.toVerifierUserInvitationDTO(adminVerifierUserInvitationDTO);

        assertNotNull(verifierUserInvitationDTO);
        assertEquals(adminVerifierUserInvitationDTO.getEmail(), verifierUserInvitationDTO.getEmail());
        assertEquals(adminVerifierUserInvitationDTO.getFirstName(), verifierUserInvitationDTO.getFirstName());
        assertEquals(adminVerifierUserInvitationDTO.getLastName(), verifierUserInvitationDTO.getLastName());
        assertEquals(adminVerifierUserInvitationDTO.getMobileNumber(), verifierUserInvitationDTO.getMobileNumber());
        assertEquals(adminVerifierUserInvitationDTO.getPhoneNumber(), verifierUserInvitationDTO.getPhoneNumber());
        assertEquals(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE, verifierUserInvitationDTO.getRoleCode());

    }

    private UserRepresentation buildUserRepresentation() {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail("username");
        userRepresentation.setId("userId");
        userRepresentation.setUsername("username");
        userRepresentation.setFirstName("FirstName");
        userRepresentation.setLastName("LastName");

        return userRepresentation;
    }
}
