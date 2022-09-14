package uk.gov.pmrv.api.user.operator.transform;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.transform.OperatorUserAcceptInvitationMapper;

class OperatorUserAcceptInvitationMapperTest {

	private OperatorUserAcceptInvitationMapper mapper;
	
	@BeforeEach
    void init() {
        mapper = Mappers.getMapper(OperatorUserAcceptInvitationMapper.class);
    }
	
	@Test
	void toOperatorInvitedUserInfoDTO() {
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation =
            OperatorUserAcceptInvitationDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .accountInstallationName("accountInstallationName")
                .build();
        String roleCode = "roleCode";
        UserInvitationStatus userInvitationStatus = UserInvitationStatus.ACCEPTED;

        OperatorInvitedUserInfoDTO expectedDto = mapper
            .toOperatorInvitedUserInfoDTO(operatorUserAcceptInvitation, roleCode, userInvitationStatus);

        assertThat(expectedDto.getFirstName()).isEqualTo(operatorUserAcceptInvitation.getFirstName());
        assertThat(expectedDto.getLastName()).isEqualTo(operatorUserAcceptInvitation.getLastName());
        assertThat(expectedDto.getEmail()).isEqualTo(operatorUserAcceptInvitation.getEmail());
        assertThat(expectedDto.getAccountInstallationName()).isEqualTo(operatorUserAcceptInvitation.getAccountInstallationName());
        assertThat(expectedDto.getRoleCode()).isEqualTo(roleCode);
        assertThat(expectedDto.getInvitationStatus()).isEqualTo(userInvitationStatus);
    }

    @Test
    void toOperatorUserAcceptInvitationDTO() {
        OperatorUserDTO operatorUser = OperatorUserDTO.builder()
            .email("email")
            .firstName("firstName")
            .lastName("lastName")
            .build();

        AuthorityInfoDTO authorityInfo = AuthorityInfoDTO.builder()
            .id(1L)
            .accountId(2L)
            .build();

        String accountInstallationName = "accountInstallationName";


        OperatorUserAcceptInvitationDTO expectedDto = mapper
            .toOperatorUserAcceptInvitationDTO(operatorUser, authorityInfo, accountInstallationName);

        assertThat(expectedDto.getEmail()).isEqualTo(operatorUser.getEmail());
        assertThat(expectedDto.getFirstName()).isEqualTo(operatorUser.getFirstName());
        assertThat(expectedDto.getLastName()).isEqualTo(operatorUser.getLastName());
        assertThat(expectedDto.getUserAuthorityId()).isEqualTo(authorityInfo.getId());
        assertThat(expectedDto.getAccountId()).isEqualTo(authorityInfo.getAccountId());
        assertThat(expectedDto.getAccountInstallationName()).isEqualTo(accountInstallationName);
        assertThat(expectedDto.getUserAuthenticationStatus()).isEqualTo(operatorUser.getStatus());
    }
}
