package uk.gov.pmrv.api.user.regulator.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RegulatorInviteUserMapper {

    @Mapping(target = "username", source = "email")
    UserRepresentation toUserRepresentation(RegulatorInvitedUserDetailsDTO regulatorInvitedUserDetailsDTO);
    
    @AfterMapping
    default void populateAttributesToUserRepresentation(
            RegulatorInvitedUserDetailsDTO regulatorInvitedUserDetailsDTO, @MappingTarget UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                        regulatorInvitedUserDetailsDTO.getPhoneNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
            regulatorInvitedUserDetailsDTO.getMobileNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(),
                regulatorInvitedUserDetailsDTO.getJobTitle());
    }
}
