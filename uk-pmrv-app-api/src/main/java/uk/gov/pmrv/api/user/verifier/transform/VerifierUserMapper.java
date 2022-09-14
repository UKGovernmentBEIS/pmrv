package uk.gov.pmrv.api.user.verifier.transform;

import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The Verifier Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface VerifierUserMapper {

    @Mapping(target = "email", source = "username")
    VerifierUserDTO toVerifierUserDTO(UserRepresentation userRepresentation);

    @AfterMapping
    default void populateAttributeToRegulatorUserDTO(UserRepresentation userRepresentation, @MappingTarget VerifierUserDTO verifierUserDTO) {
        if(ObjectUtils.isEmpty(userRepresentation.getAttributes())) {
            return;
        }
        /* Set user status */
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.USER_STATUS.getName()))
                .ifPresent(list -> verifierUserDTO.setStatus(AuthenticationStatus.valueOf(list.get(0))));

        /* Set phone number */
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.PHONE_NUMBER.getName()))
                .ifPresent(list -> verifierUserDTO.setPhoneNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        /* Set mobile number */
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.MOBILE_NUMBER.getName()))
                .ifPresent(list -> verifierUserDTO.setMobileNumber(ObjectUtils.isEmpty(list) ? null : list.get(0)));

        /* Set terms of version */
        Optional.ofNullable(userRepresentation.getAttributes().get(KeycloakUserAttributes.TERMS_VERSION.getName()))
                .ifPresent(list -> verifierUserDTO.setTermsVersion(ObjectUtils.isEmpty(list) ? null : Short.valueOf(list.get(0))));
    }

    @Mapping(target = "id", source = "userId")
    @Mapping(target = "firstName", source = "verifierUserDTO.firstName")
    @Mapping(target = "lastName", source = "verifierUserDTO.lastName")
    @Mapping(target = "email", source = "email")
    UserRepresentation toUserRepresentation(VerifierUserDTO verifierUserDTO, String userId, String username, String email, Map<String, List<String>> attributes);

    @AfterMapping
    default void populateAttributesToUserRepresentation(VerifierUserDTO verifierUserDTO, @MappingTarget UserRepresentation userRepresentation) {

        /* Set phone number */
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                verifierUserDTO.getPhoneNumber());

        /* Set mobile number */
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                verifierUserDTO.getMobileNumber());
    }

    @Mapping(target = "username", source = "email")
    UserRepresentation toUserRepresentation(VerifierUserInvitationDTO verifierUserInvitation);

    @AfterMapping
    default void populateAttributesToUserRepresentation(VerifierUserInvitationDTO verifierUserInvitation,
                                                        @MappingTarget UserRepresentation userRepresentation) {
        userRepresentation.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                verifierUserInvitation.getPhoneNumber());
        userRepresentation.singleAttribute(KeycloakUserAttributes.MOBILE_NUMBER.getName(),
                verifierUserInvitation.getMobileNumber());
    }

    @Mapping(target = "roleCode", constant = AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE)
    VerifierUserInvitationDTO toVerifierUserInvitationDTO(AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO);
}
