package uk.gov.pmrv.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.keycloak.representations.idm.UserRepresentation;

import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorUserMapperTest {

    private OperatorUserMapper mapper;

    @BeforeEach
    public void init() {
        mapper = Mappers.getMapper(OperatorUserMapper.class);
    }

    @Test
    void toOperatorUserDTO() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.USER_STATUS.getName(), List.of(AuthenticationStatus.REGISTERED.name()));
            put(KeycloakUserAttributes.TERMS_VERSION.getName(), List.of("1"));
            put(KeycloakUserAttributes.LINE_1.getName(), List.of("line1"));
            put(KeycloakUserAttributes.LINE_2.getName(), List.of("line2"));
            put(KeycloakUserAttributes.COUNTRY.getName(), List.of("GR"));
            put(KeycloakUserAttributes.CITY.getName(), List.of("city"));
            put(KeycloakUserAttributes.POSTCODE.getName(), List.of("postcode"));
            put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
            put(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.MOBILE_NUMBER.getName(), List.of("2101313131"));
        }});

        // Invoke
        OperatorUserDTO operatorUserDTO = mapper.toOperatorUserDTO(userRepresentation);

        // Assert
        assertEquals(userRepresentation.getEmail(), operatorUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), operatorUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), operatorUserDTO.getLastName());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0), operatorUserDTO.getStatus().name());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0), operatorUserDTO.getTermsVersion().toString());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_1.getName()).get(0), operatorUserDTO.getAddress().getLine1());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_2.getName()).get(0), operatorUserDTO.getAddress().getLine2());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.COUNTRY.getName()).get(0), operatorUserDTO.getAddress().getCountry());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.CITY.getName()).get(0), operatorUserDTO.getAddress().getCity());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.POSTCODE.getName()).get(0), operatorUserDTO.getAddress().getPostcode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getPhoneNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), operatorUserDTO.getPhoneNumber().getNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getMobileNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), operatorUserDTO.getMobileNumber().getNumber());
    }

    @Test
    void toOperatorUserDTO_no_address() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.USER_STATUS.getName(), List.of(AuthenticationStatus.REGISTERED.name()));
            put(KeycloakUserAttributes.TERMS_VERSION.getName(), List.of("1"));
            put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
            put(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.MOBILE_NUMBER.getName(), List.of("2101313131"));
        }});

        // Invoke
        OperatorUserDTO operatorUserDTO = mapper.toOperatorUserDTO(userRepresentation);

        // Assert
        assertEquals(userRepresentation.getEmail(), operatorUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), operatorUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), operatorUserDTO.getLastName());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0), operatorUserDTO.getStatus().name());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0), operatorUserDTO.getTermsVersion().toString());
        assertNull(operatorUserDTO.getAddress().getLine1());
        assertNull(operatorUserDTO.getAddress().getLine2());
        assertNull(operatorUserDTO.getAddress().getCountry());
        assertNull(operatorUserDTO.getAddress().getCity());
        assertNull(operatorUserDTO.getAddress().getPostcode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getPhoneNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), operatorUserDTO.getPhoneNumber().getNumber());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getMobileNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0), operatorUserDTO.getMobileNumber().getNumber());
    }

    @Test
    void toOperatorUserDTO_no_mobile() {
        UserRepresentation userRepresentation = buildUserRepresentation();
        userRepresentation.setAttributes(new HashMap<>(){{
            put(KeycloakUserAttributes.USER_STATUS.getName(), List.of(AuthenticationStatus.REGISTERED.name()));
            put(KeycloakUserAttributes.TERMS_VERSION.getName(), List.of("1"));
            put(KeycloakUserAttributes.LINE_1.getName(), List.of("line1"));
            put(KeycloakUserAttributes.LINE_2.getName(), List.of("line2"));
            put(KeycloakUserAttributes.COUNTRY.getName(), List.of("GR"));
            put(KeycloakUserAttributes.CITY.getName(), List.of("city"));
            put(KeycloakUserAttributes.POSTCODE.getName(), List.of("postcode"));
            put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(), List.of("GR"));
            put(KeycloakUserAttributes.PHONE_NUMBER.getName(), List.of("2101313131"));
        }});

        // Invoke
        OperatorUserDTO operatorUserDTO = mapper.toOperatorUserDTO(userRepresentation);

        // Assert
        assertEquals(userRepresentation.getEmail(), operatorUserDTO.getEmail());
        assertEquals(userRepresentation.getFirstName(), operatorUserDTO.getFirstName());
        assertEquals(userRepresentation.getLastName(), operatorUserDTO.getLastName());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0), operatorUserDTO.getStatus().name());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0), operatorUserDTO.getTermsVersion().toString());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_1.getName()).get(0), operatorUserDTO.getAddress().getLine1());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_2.getName()).get(0), operatorUserDTO.getAddress().getLine2());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.COUNTRY.getName()).get(0), operatorUserDTO.getAddress().getCountry());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.CITY.getName()).get(0), operatorUserDTO.getAddress().getCity());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.POSTCODE.getName()).get(0), operatorUserDTO.getAddress().getPostcode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0), operatorUserDTO.getPhoneNumber().getCountryCode());
        assertEquals(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0), operatorUserDTO.getPhoneNumber().getNumber());
        assertNull(operatorUserDTO.getMobileNumber().getCountryCode());
        assertNull(operatorUserDTO.getMobileNumber().getNumber());
    }

    @Test
    void toUserRepresentation() {
        String userId = "userId";
        String username = "username";
        String status = AuthenticationStatus.REGISTERED.name();
        String terms = "1";
        Map<String, List<String>> attributes = Map.of(KeycloakUserAttributes.USER_STATUS.getName(), List.of(status),
                KeycloakUserAttributes.TERMS_VERSION.getName(), List.of(terms));

        OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
                .email("fromUI")
                .firstName("firstName")
                .lastName("lastName")
                .address(AddressDTO.builder()
                        .line1("line1")
                        .line2("line2")
                        .country("GR")
                        .city("city")
                        .postcode("postcode")
                        .build())
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .mobileNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(operatorUserDTO, userId, username, username, attributes);

        // Assert
        assertEquals(userId, userRepresentation.getId());
        assertEquals(username, userRepresentation.getUsername());
        assertEquals(username, userRepresentation.getEmail());
        assertEquals(operatorUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(operatorUserDTO.getLastName(), userRepresentation.getLastName());
        assertEquals(status, userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0));
        assertEquals(terms, userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getLine1(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.LINE_1.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getLine2(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.LINE_2.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getCountry(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.COUNTRY.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getCity(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.CITY.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getPostcode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.POSTCODE.getName()).get(0));
        assertEquals(operatorUserDTO.getPhoneNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getPhoneNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(operatorUserDTO.getMobileNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getMobileNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toUserRepresentation_no_address() {
        String userId = "userId";
        String username = "username";
        String status = AuthenticationStatus.REGISTERED.name();
        String terms = "1";
        Map<String, List<String>> attributes = Map.of(KeycloakUserAttributes.USER_STATUS.getName(), List.of(status),
                KeycloakUserAttributes.TERMS_VERSION.getName(), List.of(terms));

        OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
                .email(username)
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .mobileNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(operatorUserDTO, userId, username, username, attributes);

        // Assert
        assertEquals(userId, userRepresentation.getId());
        assertEquals(username, userRepresentation.getUsername());
        assertEquals(username, userRepresentation.getEmail());
        assertEquals(operatorUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(operatorUserDTO.getLastName(), userRepresentation.getLastName());
        assertEquals(status, userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0));
        assertEquals(terms, userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0));
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_1.getName()).isEmpty());
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_2.getName()).isEmpty());
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.COUNTRY.getName()).isEmpty());
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.CITY.getName()).isEmpty());
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.POSTCODE.getName()).isEmpty());
        assertEquals(operatorUserDTO.getPhoneNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getPhoneNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertEquals(operatorUserDTO.getMobileNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getMobileNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).get(0));
    }

    @Test
    void toUserRepresentation_no_mobile() {
        String userId = "userId";
        String username = "username";
        String status = AuthenticationStatus.REGISTERED.name();
        String terms = "1";
        Map<String, List<String>> attributes = Map.of(KeycloakUserAttributes.USER_STATUS.getName(), List.of(status),
                KeycloakUserAttributes.TERMS_VERSION.getName(), List.of(terms));

        OperatorUserDTO operatorUserDTO = OperatorUserDTO.builder()
                .email(username)
                .firstName("firstName")
                .lastName("lastName")
                .address(AddressDTO.builder()
                        .line1("line1")
                        .line2("line2")
                        .country("GR")
                        .city("city")
                        .postcode("postcode")
                        .build())
                .phoneNumber(PhoneNumberDTO.builder()
                        .countryCode("GR")
                        .number("2101313131")
                        .build())
                .build();

        // Invoke
        UserRepresentation userRepresentation = mapper.toUserRepresentation(operatorUserDTO, userId, username, username, attributes);

        // Assert
        assertEquals(userId, userRepresentation.getId());
        assertEquals(username, userRepresentation.getUsername());
        assertEquals(username, userRepresentation.getEmail());
        assertEquals(operatorUserDTO.getFirstName(), userRepresentation.getFirstName());
        assertEquals(operatorUserDTO.getLastName(), userRepresentation.getLastName());
        assertEquals(status, userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()).get(0));
        assertEquals(terms, userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getLine1(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.LINE_1.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getLine2(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.LINE_2.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getCountry(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.COUNTRY.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getCity(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.CITY.getName()).get(0));
        assertEquals(operatorUserDTO.getAddress().getPostcode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.POSTCODE.getName()).get(0));
        assertEquals(operatorUserDTO.getPhoneNumber().getCountryCode(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()).get(0));
        assertEquals(operatorUserDTO.getPhoneNumber().getNumber(), userRepresentation.getAttributes()
                .get(KeycloakUserAttributes.PHONE_NUMBER.getName()).get(0));
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()).isEmpty());
        assertTrue(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()).isEmpty());
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
