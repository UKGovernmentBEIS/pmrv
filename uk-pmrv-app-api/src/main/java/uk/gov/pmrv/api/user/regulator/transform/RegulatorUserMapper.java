package uk.gov.pmrv.api.user.regulator.transform;

import org.keycloak.representations.idm.UserRepresentation;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RegulatorUserMapper {

    @Mapping(target = "email", source = "userRepresentation.username")
    RegulatorUserDTO toRegulatorUserDTO(UserRepresentation userRepresentation, FileInfoDTO signature);

    @AfterMapping
    default void populateAttributeToRegulatorUserDTO(UserRepresentation userRepresentation, @MappingTarget RegulatorUserDTO regulatorUserDTO) {
        if(ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()))
                .ifPresent(list -> regulatorUserDTO.setStatus(AuthenticationStatus.valueOf(list.get(0))));

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.JOB_TITLE.getName()))
                .ifPresent(list -> regulatorUserDTO.setJobTitle(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> regulatorUserDTO.setPhoneNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> regulatorUserDTO.setMobileNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()))
                .ifPresent(list -> regulatorUserDTO.setTermsVersion(ObjectUtils.isEmpty(list) ? null : Short.valueOf(list.get(0))));
    }

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "firstName", source = "regulatorUserDTO.firstName")
    @Mapping(target = "lastName", source = "regulatorUserDTO.lastName")
    @Mapping(target = "email", source = "email")
    UserRepresentation toUserRepresentation(RegulatorUserDTO regulatorUserDTO, String userId, String username, String email, Map<String, List<String>> attributes);

    @AfterMapping
    default void populateAttributesToUserRepresentation(RegulatorUserDTO regulatorUserDTO, @MappingTarget UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(), regulatorUserDTO.getJobTitle());
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(), regulatorUserDTO.getPhoneNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(), regulatorUserDTO.getMobileNumber());
    }
    
}
