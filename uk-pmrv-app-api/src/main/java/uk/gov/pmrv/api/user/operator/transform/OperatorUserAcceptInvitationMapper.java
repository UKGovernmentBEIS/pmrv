package uk.gov.pmrv.api.user.operator.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface OperatorUserAcceptInvitationMapper {

	 OperatorInvitedUserInfoDTO toOperatorInvitedUserInfoDTO(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation,
                                                             String roleCode, UserInvitationStatus invitationStatus);

	 @Mapping(target = "userAuthorityId", source = "authorityInfoDTO.id")
     @Mapping(target = "userAuthenticationStatus", source = "operatorUserDTO.status")
     @Mapping(target = "userId", source = "authorityInfoDTO.userId")
	 OperatorUserAcceptInvitationDTO toOperatorUserAcceptInvitationDTO(OperatorUserDTO operatorUserDTO,
                                                                       AuthorityInfoDTO authorityInfoDTO,
                                                                       String accountInstallationName);
}
