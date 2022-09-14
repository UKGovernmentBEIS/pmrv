package uk.gov.pmrv.api.user.operator.transform;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorUserRegistrationMapperTest {

    private OperatorUserRegistrationMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(OperatorUserRegistrationMapper.class);
    }
    
	@Test
	void toUserRepresentation_when_operatorUserRegistrationWithCredentialsDTO_with_address() {
		String fn = "fn";
		String ln = "ln";
		String line1 = "line1";
		String city = "city";
		String country = "GR";
		String postCode = "post";
		String password = "password";
		OperatorUserRegistrationWithCredentialsDTO
            userRegistrationDTO = createUserRegistrationWithCredentialsDTO(fn, ln, line1, null, city, country, postCode, password);
		
		//invoke
		UserRepresentation userRepresentation = 
				mapper.toUserRepresentation(userRegistrationDTO, "email");
		
		//assert
		assertThat(userRepresentation.getFirstName()).isEqualTo(fn);
		assertThat(userRepresentation.getLastName()).isEqualTo(ln);
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_1.getName()).get(0)).isEqualTo(line1);
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_2.getName())).isNull();
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.CITY.getName()).get(0)).isEqualTo(city);
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.COUNTRY.getName()).get(0)).isEqualTo(country);
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.POSTCODE.getName()).get(0)).isEqualTo(postCode);
        assertThat(userRepresentation.getCredentials()).hasSize(1);
        assertThat(userRepresentation.getCredentials()).containsOnlyElementsOf(getPasswordCredentials(password));
	}
	
	@Test
	void toUserRepresentation_when_operatorUserRegistrationWithCredentialsDTO_no_address() {
		String fn = "fn";
		String ln = "ln";
        String password = "password";
		OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO = createUserRegistrationWithCredentialsDTO(fn, ln, password);
		
		//invoke
		UserRepresentation userRepresentation = 
				mapper.toUserRepresentation(userRegistrationDTO, "email");
		
		//assert
		assertThat(userRepresentation.getFirstName()).isEqualTo(fn);
		assertThat(userRepresentation.getLastName()).isEqualTo(ln);
		assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.LINE_1.getName());
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_1.getName())).isNull();
		assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.LINE_2.getName());
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_2.getName())).isNull();
		assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.CITY.getName());
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.CITY.getName())).isNull();
		assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.COUNTRY.getName());
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.COUNTRY.getName())).isNull();
		assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.POSTCODE.getName());
		assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.POSTCODE.getName())).isNull();
		assertThat(userRepresentation.getCredentials()).hasSize(1);
		assertThat(userRepresentation.getCredentials()).containsOnlyElementsOf(getPasswordCredentials(password));
	}

    @Test
    void toUserRepresentation_when_operatorUserRegistrationDTO() {
        String firstName = "firstName";
        String lastName = "lastName";
        String userId = "userId";
        String userEmail = "userEmail";
        OperatorUserRegistrationDTO userRegistrationDTO = OperatorUserRegistrationDTO.builder()
            .emailToken("emailToken")
            .firstName(firstName)
            .lastName(lastName)
            .phoneNumber(PhoneNumberDTO.builder().countryCode("GR").number("123").build())
            .termsVersion(Short.valueOf((short) 1))
            .build();

        //invoke
        UserRepresentation userRepresentation =
            mapper.toUserRepresentation(userRegistrationDTO, userEmail, userId);

        //assert
        assertThat(userRepresentation.getFirstName()).isEqualTo(firstName);
        assertThat(userRepresentation.getLastName()).isEqualTo(lastName);
        assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.LINE_1.getName());
        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_1.getName())).isNull();
        assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.LINE_2.getName());
        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_2.getName())).isNull();
        assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.CITY.getName());
        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.CITY.getName())).isNull();
        assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.COUNTRY.getName());
        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.COUNTRY.getName())).isNull();
        assertThat(userRepresentation.getAttributes()).containsKey(KeycloakUserAttributes.POSTCODE.getName());
        assertThat(userRepresentation.getAttributes().get(KeycloakUserAttributes.POSTCODE.getName())).isNull();
        assertThat(userRepresentation.getCredentials()).isNull();
    }
	
	private OperatorUserRegistrationWithCredentialsDTO createUserRegistrationWithCredentialsDTO(String firstName,
                                                                                                String lastName,
                                                                                                String password) {
		return OperatorUserRegistrationWithCredentialsDTO.builder()
				.password(password)
				.emailToken("dsdsd")
				.firstName(firstName)
				.lastName(lastName)
				.phoneNumber(PhoneNumberDTO.builder().countryCode("GR").number("123").build())
				.termsVersion(Short.valueOf((short)1))
				.build();
	}
	
	private OperatorUserRegistrationWithCredentialsDTO createUserRegistrationWithCredentialsDTO(
			String firstName, String lastName,
			String line1, String line2, String city, String country, String postCode, String password) {
		return OperatorUserRegistrationWithCredentialsDTO.builder()
				.password(password)
				.emailToken("dsdsd")
				.firstName(firstName)
				.lastName(lastName)
				.phoneNumber(PhoneNumberDTO.builder().countryCode("GR").number("123").build())
				.address(AddressDTO.builder().line1(line1).line2(line2).city(city).country(country).postcode(postCode).build())
				.termsVersion(Short.valueOf((short)1))
				.build();
	}

	private List<CredentialRepresentation> getPasswordCredentials(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        return List.of(credentialRepresentation);
    }
}
