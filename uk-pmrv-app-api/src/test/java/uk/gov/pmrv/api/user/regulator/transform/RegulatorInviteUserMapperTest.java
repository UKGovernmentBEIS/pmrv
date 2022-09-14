package uk.gov.pmrv.api.user.regulator.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.regulator.transform.RegulatorInviteUserMapper;

class RegulatorInviteUserMapperTest {

    private RegulatorInviteUserMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(RegulatorInviteUserMapper.class);
    }

    @Test
    void toUserRepresentation() {
        RegulatorInvitedUserDetailsDTO regulatorInvitedUserDetails =
            RegulatorInvitedUserDetailsDTO.builder()
                .firstName("fn")
                .lastName("ln")
                .email("em@em.gr")
                .jobTitle("title")
                .phoneNumber("210000")
                .mobileNumber("699999")
                .build();

        UserRepresentation userRepresentation = mapper.toUserRepresentation(regulatorInvitedUserDetails);

        assertNotNull(userRepresentation);
        assertEquals(userRepresentation.getEmail(), regulatorInvitedUserDetails.getEmail());
        assertEquals(userRepresentation.getUsername(), regulatorInvitedUserDetails.getEmail());
        assertEquals(userRepresentation.getFirstName(), regulatorInvitedUserDetails.getFirstName());
        assertEquals(userRepresentation.getLastName(), regulatorInvitedUserDetails.getLastName());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), regulatorInvitedUserDetails.getPhoneNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), regulatorInvitedUserDetails.getMobileNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()).get(0), regulatorInvitedUserDetails.getJobTitle());
    }

}