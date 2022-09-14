package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserUpdateDTO;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegulatorUserManagementServiceTest {

	@InjectMocks
    private RegulatorUserManagementService service;

	@Mock
	private RegulatorAuthorityService regulatorAuthorityService;

	@Mock
	private RegulatorUserAuthService regulatorUserAuthService;

	@Test
	void getRegulatorUserByUserId() {
		final String userId = "userId";
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        PmrvAuthority pmrvAuthority = createRegulatorAuthority("code1", ca);
        PmrvUser authUser = buildRegulatorUser("reg1Id", "reg1", List.of(pmrvAuthority));

		when(regulatorAuthorityService.existsByUserIdAndCompetentAuthority(userId, ca)).thenReturn(true);
		when(regulatorUserAuthService.getRegulatorUserById(userId)).thenReturn(RegulatorUserDTO.builder().build());

		// Invoke
		service.getRegulatorUserByUserId(authUser, userId);

		// Assert
		verify(regulatorAuthorityService, times(1)).existsByUserIdAndCompetentAuthority(userId, ca);
		verify(regulatorUserAuthService, times(1)).getRegulatorUserById(userId);
	}

	@Test
	void getRegulatorUserByUserId_user_not_belongs_to_ca() {
		final String userId = "userId";
		final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        PmrvAuthority pmrvAuthority = createRegulatorAuthority("code1", ca);
        PmrvUser authUser = buildRegulatorUser("reg1Id", "reg1", List.of(pmrvAuthority));

		when(regulatorAuthorityService.existsByUserIdAndCompetentAuthority(userId, ca)).thenReturn(false);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.getRegulatorUserByUserId(authUser, userId));

		// Assert
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA, businessException.getErrorCode());
		verify(regulatorAuthorityService, times(1)).existsByUserIdAndCompetentAuthority(userId, ca);
		verify(regulatorUserAuthService, never()).getRegulatorUserById(anyString());
	}

	@Test
	void updateRegulatorUserByUserId() {
		final String userId = "userId";
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        PmrvAuthority pmrvAuthority = createRegulatorAuthority("code1", ca);
        PmrvUser authUser = buildRegulatorUser("reg1Id", "reg1", List.of(pmrvAuthority));
		RegulatorUserDTO regulatorUserDTO = RegulatorUserDTO.builder().build();
		
		FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();

		when(regulatorAuthorityService.existsByUserIdAndCompetentAuthority(userId, ca)).thenReturn(true);

		// Invoke
		service.updateRegulatorUserByUserId(authUser, userId, regulatorUserDTO, signature);

		// Assert
		verify(regulatorAuthorityService, times(1)).existsByUserIdAndCompetentAuthority(userId, ca);
		verify(regulatorUserAuthService, times(1)).updateRegulatorUser(userId, regulatorUserDTO, signature);
	}

	@Test
	void updateRegulatorUserByUserId_user_not_belongs_to_ca() {
		final String userId = "userId";
        final CompetentAuthority ca = CompetentAuthority.ENGLAND;
        PmrvAuthority pmrvAuthority = createRegulatorAuthority("code1", ca);
        PmrvUser authUser = buildRegulatorUser("reg1Id", "reg1", List.of(pmrvAuthority));
        RegulatorUserDTO regulatorUserDTO = RegulatorUserDTO.builder().build();
        
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();

		when(regulatorAuthorityService.existsByUserIdAndCompetentAuthority(userId, ca)).thenReturn(false);

		// Invoke
		BusinessException businessException = assertThrows(BusinessException.class,
				() -> service.updateRegulatorUserByUserId(authUser, userId, regulatorUserDTO, signature));

		// Assert
		assertEquals(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA, businessException.getErrorCode());
		verify(regulatorAuthorityService, times(1)).existsByUserIdAndCompetentAuthority(userId, ca);
		verify(regulatorUserAuthService, never()).updateRegulatorUser(anyString(), any(), any());
	}

	@Test
	void updateCurrentRegulatorUser() {
		PmrvUser authUser = PmrvUser.builder().userId("authId").roleType(RoleType.REGULATOR)
				.authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build())).build();
		RegulatorUserUpdateDTO regulator = RegulatorUserUpdateDTO.builder()
				.user(RegulatorUserDTO.builder().build()).permissions(Map.of()).build();
		
		FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();

		// Invoke
		service.updateCurrentRegulatorUser(authUser, regulator, signature);

		// Assert
		verify(regulatorUserAuthService, times(1)).updateRegulatorUser(authUser.getUserId(), regulator.getUser(), signature);
	}

	private PmrvUser buildRegulatorUser(String userId, String username, List<PmrvAuthority> pmrvAuthorities) {

		return PmrvUser.builder()
				.userId(userId)
				.firstName(username)
				.lastName(username)
				.authorities(pmrvAuthorities)
				.roleType(RoleType.REGULATOR)
				.build();
	}

	private PmrvAuthority createRegulatorAuthority(String code, CompetentAuthority competentAuthority) {
		return PmrvAuthority.builder()
				.code(code)
				.competentAuthority(competentAuthority)
				.build();
	}
}
