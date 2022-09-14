package uk.gov.pmrv.api.user.regulator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.keycloak.representations.idm.UserRepresentation;

import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegulatorUserMapperTest {

    private RegulatorUserMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RegulatorUserMapper.class);
    }

    @Test
    void toRegulatorUserDTO() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.USER_STATUS.getName(), List.of(AuthenticationStatus.REGISTERED.name()));
            put(KeycloakUserAttributes.TERMS_VERSION.getName(), List.of("1"));
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
            put(KeycloakUserAttributes.MOBILE_NUMBER.getName(), List.of("699999999"));
            put(KeycloakUserAttributes.JOB_TITLE.getName(), List.of("jobTitle"));
        }});
        
        String signatureUuid = UUID.randomUUID().toString();
        FileInfoDTO signature = FileInfoDTO.builder()
                .name("sign").uuid(signatureUuid)
                .build();

        // Invoke
        RegulatorUserDTO regulatorUserDTO = mapper.toRegulatorUserDTO(userRepresentation, signature);

        // Assert
        assertEquals(userRepresentation.getEmail(), regulatorUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), regulatorUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), regulatorUserDTO.getLastName());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0), regulatorUserDTO.getStatus().name());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0), regulatorUserDTO.getTermsVersion().toString());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), regulatorUserDTO.getPhoneNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), regulatorUserDTO.getMobileNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()).get(0), regulatorUserDTO.getJobTitle());
        assertThat(regulatorUserDTO.getSignature().getName()).isEqualTo("sign");
        assertThat(regulatorUserDTO.getSignature().getUuid()).isEqualTo(signatureUuid);
    }

    @Test
    void toUserRepresentation() {
        String userId = "userId";
        String username = "username";
        String status = AuthenticationStatus.REGISTERED.name();
        String terms = "1";
        Map<String, List<String>> attributes = Map.of(KeycloakUserAttributes.USER_STATUS.getName(), List.of(status),
                KeycloakUserAttributes.TERMS_VERSION.getName(), List.of(terms));

        RegulatorUserDTO regulatorUserDTO = RegulatorUserDTO.builder()
                .email("fromUI")
                .firstName("firstName")
                .lastName("lastName")
                .jobTitle("jobTitle")
                .phoneNumber("2101313131")
                .mobileNumber("699999999")
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(regulatorUserDTO, userId, username, username, attributes);

        // Assert
        assertEquals(userId, userRepresentation.getId());
        assertEquals(username, userRepresentation.getUsername());
        assertEquals(username, userRepresentation.getEmail());
        assertEquals(regulatorUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(regulatorUserDTO.getLastName(), userRepresentation.getLastName());
        assertEquals(status, userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0));
        assertEquals(terms, userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0));
        assertEquals(regulatorUserDTO.getJobTitle(), userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()).get(0));
        assertEquals(regulatorUserDTO.getPhoneNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(regulatorUserDTO.getMobileNumber(), userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
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
