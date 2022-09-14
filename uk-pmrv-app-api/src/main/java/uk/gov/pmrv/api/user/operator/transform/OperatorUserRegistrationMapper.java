package uk.gov.pmrv.api.user.operator.transform;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface OperatorUserRegistrationMapper {
	
	@Mapping(source = "email", target = "username")
    @Mapping(source = "email", target = "email")
    UserRepresentation toUserRepresentation(@Valid OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO, String email);

	@Mapping(source = "email", target = "username")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "userId", target = "id")
	UserRepresentation toUserRepresentation(
        OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO, String email, String userId);

    @AfterMapping
    default void populateAttributesToUserRepresentation(
        OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO, @MappingTarget UserRepresentation userRepresentation) {
        populateUserRepresentationAttributes(operatorUserRegistrationWithCredentialsDTO, userRepresentation);

        /* Set credentials */
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setTemporary(false);
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setValue(operatorUserRegistrationWithCredentialsDTO.getPassword());
        userRepresentation.setCredentials(asList(credentials));
    }

    @Mapping(source = "email", target = "username")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "userId", target = "id")
    UserRepresentation toUserRepresentation(OperatorUserRegistrationDTO operatorUserRegistrationDTO, String email, String userId);

    @AfterMapping
    default void populateAttributesToUserRepresentation(OperatorUserRegistrationDTO operatorUserRegistrationDTO,
                                                        @MappingTarget UserRepresentation userRepresentation) {
        populateUserRepresentationAttributes(operatorUserRegistrationDTO, userRepresentation);
    }

    private void populateUserRepresentationAttributes(OperatorUserRegistrationDTO operatorUserRegistrationDTO,
                                                      UserRepresentation userRepresentation) {
        Map<String, List<String>> attributes = new HashMap<>();

        attributes.put(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
            asList(operatorUserRegistrationDTO.getPhoneNumber().getCountryCode()));
        attributes.put(KeycloakUserAttributes.PHONE_NUMBER.getName(),
            asList(operatorUserRegistrationDTO.getPhoneNumber().getNumber()));
        attributes.put(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
            (Objects.isNull(operatorUserRegistrationDTO.getMobileNumber()) ||
                ObjectUtils.isEmpty(operatorUserRegistrationDTO.getMobileNumber().getCountryCode())) ? null
                : asList(operatorUserRegistrationDTO.getMobileNumber().getCountryCode()));
        attributes.put(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
            (Objects.isNull(operatorUserRegistrationDTO.getMobileNumber()) ||
                ObjectUtils.isEmpty(operatorUserRegistrationDTO.getMobileNumber().getNumber())) ? null
                : asList(operatorUserRegistrationDTO.getMobileNumber().getNumber()));
        attributes.put(KeycloakUserAttributes.TERMS_VERSION.getName(),
            ObjectUtils.isEmpty(operatorUserRegistrationDTO.getTermsVersion()) ? null
                : asList(String.valueOf(operatorUserRegistrationDTO.getTermsVersion())));

        //address
        attributes.put(KeycloakUserAttributes.LINE_1.getName(),
            Objects.isNull(operatorUserRegistrationDTO.getAddress()) ? null
                : asList(operatorUserRegistrationDTO.getAddress().getLine1()));
        attributes.put(KeycloakUserAttributes.LINE_2.getName(),
            (Objects.isNull(operatorUserRegistrationDTO.getAddress()) ||
                ObjectUtils.isEmpty(operatorUserRegistrationDTO.getAddress().getLine2())) ? null
                : asList(operatorUserRegistrationDTO.getAddress().getLine2()));
        attributes.put(KeycloakUserAttributes.CITY.getName(),
            Objects.isNull(operatorUserRegistrationDTO.getAddress()) ? null
                : asList(operatorUserRegistrationDTO.getAddress().getCity()));
        attributes.put(KeycloakUserAttributes.COUNTRY.getName(),
            Objects.isNull(operatorUserRegistrationDTO.getAddress()) ? null
                : asList(operatorUserRegistrationDTO.getAddress().getCountry()));
        attributes.put(KeycloakUserAttributes.POSTCODE.getName(),
            Objects.isNull(operatorUserRegistrationDTO.getAddress()) ? null
                : asList(operatorUserRegistrationDTO.getAddress().getPostcode()));

        userRepresentation.setAttributes(attributes);
    }
    
    private <T> List<T> asList(T value){
    	return List.of(value);
    }
}
