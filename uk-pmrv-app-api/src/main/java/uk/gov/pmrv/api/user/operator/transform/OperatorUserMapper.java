package uk.gov.pmrv.api.user.operator.transform;

import org.keycloak.representations.idm.UserRepresentation;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.dto.PhoneNumberDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Operator Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface OperatorUserMapper {

    @Mapping(target = "email", source = "username")
    OperatorUserDTO toOperatorUserDTO(UserRepresentation userRepresentation);

    @AfterMapping
    default void populateAttributeToOperatorUserDTO(UserRepresentation userRepresentation, @MappingTarget OperatorUserDTO operatorUserDTO) {
        if(ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()))
                .ifPresent(list -> operatorUserDTO.setStatus(AuthenticationStatus.valueOf(list.get(0))));

        /* Set Address */
        AddressDTO addressDTO = new AddressDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_1.getName()))
                .ifPresent(list -> addressDTO.setLine1(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.LINE_2.getName()))
                .ifPresent(list -> addressDTO.setLine2(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.CITY.getName()))
                .ifPresent(list -> addressDTO.setCity(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.COUNTRY.getName()))
                .ifPresent(list -> addressDTO.setCountry(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.POSTCODE.getName()))
                .ifPresent(list -> addressDTO.setPostcode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        operatorUserDTO.setAddress(addressDTO);

        /* Set phone number */
        PhoneNumberDTO phoneNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName()))
                .ifPresent(list -> phoneNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> phoneNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        operatorUserDTO.setPhoneNumber(phoneNumber);

        /* Set Mobile number */
        PhoneNumberDTO mobileNumber = new PhoneNumberDTO();
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName()))
                .ifPresent(list -> mobileNumber.setCountryCode(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> mobileNumber.setNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));
        operatorUserDTO.setMobileNumber(mobileNumber);

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()))
                .ifPresent(list -> operatorUserDTO.setTermsVersion(ObjectUtils.isEmpty(list) ? null : Short.valueOf(list.get(0))));
    }

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "firstName", source = "operatorUserDTO.firstName")
    @Mapping(target = "lastName", source = "operatorUserDTO.lastName")
    @Mapping(target = "email", source = "email")
    UserRepresentation toUserRepresentation(OperatorUserDTO operatorUserDTO, String userId, String username, String email, Map<String, List<String>> attributes);

    @AfterMapping
    default void populateAttributesToUserRepresentation(OperatorUserDTO operatorUserDTO, @MappingTarget UserRepresentation userRepresentation) {

        // Set phone numbers
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER_CODE.getName(),
                operatorUserDTO.getPhoneNumber().getCountryCode());
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                operatorUserDTO.getPhoneNumber().getNumber());

        Optional.ofNullable(operatorUserDTO.getMobileNumber()).ifPresentOrElse(phoneNumberDTO -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(),
                    phoneNumberDTO.getCountryCode());
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                    phoneNumberDTO.getNumber());
        }, () -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER_CODE.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), null);
        });

        // Set address
        Optional.ofNullable(operatorUserDTO.getAddress()).ifPresentOrElse(address -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.LINE_1.getName(), address.getLine1());
            userRepresentation.singleAttribute(KeycloakUserAttributes.LINE_2.getName(), address.getLine2());
            userRepresentation.singleAttribute(KeycloakUserAttributes.CITY.getName(), address.getCity());
            userRepresentation.singleAttribute(KeycloakUserAttributes.COUNTRY.getName(), address.getCountry());
            userRepresentation.singleAttribute(KeycloakUserAttributes.POSTCODE.getName(), address.getPostcode());
        }, () -> {
            userRepresentation.singleAttribute(KeycloakUserAttributes.LINE_1.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.LINE_2.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.CITY.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.COUNTRY.getName(), null);
            userRepresentation.singleAttribute(KeycloakUserAttributes.POSTCODE.getName(), null);
        });
    }

    @Mapping(target = "username", source = "email")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    UserRepresentation toUserRepresentation(String email, String firstName, String lastName);
}
