package uk.gov.pmrv.api.user.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.KeycloakUserAttributes;
import uk.gov.pmrv.api.user.core.domain.model.UserDetails;
import uk.gov.pmrv.api.user.core.domain.model.UserDetailsRequest;
import uk.gov.pmrv.api.user.core.domain.model.core.SignatureRequest;
import uk.gov.pmrv.api.user.core.service.UserSignatureValidatorService;
import uk.gov.pmrv.api.user.core.service.auth.AuthService;
import uk.gov.pmrv.api.user.core.service.auth.UserDetailsSaveException;
import uk.gov.pmrv.api.user.core.service.auth.UserRegistrationService;
import uk.gov.pmrv.api.user.core.transform.UserDetailsMapper;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.regulator.transform.RegulatorInviteUserMapper;
import uk.gov.pmrv.api.user.regulator.transform.RegulatorUserMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RegulatorUserAuthServiceTest {

	@InjectMocks
    private RegulatorUserAuthService service;

	@Mock
	private AuthService authService;
	
	@Mock
	private UserRegistrationService userRegistrationService;
	
	@Mock
	private UserSignatureValidatorService userSignatureValidatorService;
	
	@Mock
    private RegulatorUserMapper regulatorUserMapper;
    
    @Mock
    private RegulatorInviteUserMapper regulatorInviteUserMapper;
    
    @Mock
    private UserDetailsMapper userDetailsMapper;
	
	@Test
    void getRegulatorUserById() {
        String userId = "userId";
        String username = "username";
        
        UserRepresentation userRepresentation = createUserRepresentation(userId, "email1", username);
        UserDetails userDetails = UserDetails.builder()
                .id(userId)
                .signature(FileInfoDTO.builder().name("signature").uuid("signuuid").build())
                .build();
        RegulatorUserDTO regulatorUserDTO = RegulatorUserDTO.builder()
                .firstName("fn").lastName("ln")
                .signature(userDetails.getSignature())
                .build();
        
        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(authService.getUserDetails(userId)).thenReturn(Optional.of(userDetails));
        when(regulatorUserMapper.toRegulatorUserDTO(userRepresentation, userDetails.getSignature())).thenReturn(regulatorUserDTO);
        
        RegulatorUserDTO result = service.getRegulatorUserById(userId);
        
        assertThat(result).isEqualTo(regulatorUserDTO);
        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(authService, times(1)).getUserDetails(userId);
        verify(regulatorUserMapper, times(1)).toRegulatorUserDTO(userRepresentation, userDetails.getSignature());
    }

	@Test
	void registerRegulatorInvitedUser() throws UserDetailsSaveException {
		String email = "email";
		RegulatorInvitedUserDetailsDTO regulatorInvitedUserDetailsDTO =
				RegulatorInvitedUserDetailsDTO.builder()
						.email(email).firstName("fn").lastName("ln").jobTitle("jt").phoneNumber("210000").build();
		final String userId = "user";

		UserRepresentation keycloakUser = new UserRepresentation();
		keycloakUser.setEmail(regulatorInvitedUserDetailsDTO.getEmail());
		keycloakUser.setFirstName(regulatorInvitedUserDetailsDTO.getFirstName());
		keycloakUser.setLastName(regulatorInvitedUserDetailsDTO.getLastName());
		keycloakUser.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
				regulatorInvitedUserDetailsDTO.getPhoneNumber());
		keycloakUser.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(),
				regulatorInvitedUserDetailsDTO.getJobTitle());
		
		FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();

		UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
        .signature(SignatureRequest.builder()
                .content("content".getBytes())
                .name("signature")
                .size(1L)
                .type("type")
                .build())
        .build();
		
		when(regulatorInviteUserMapper.toUserRepresentation(regulatorInvitedUserDetailsDTO)).thenReturn(keycloakUser);
		when(userRegistrationService.registerInvitedUser(keycloakUser)).thenReturn(userId);
		when(userDetailsMapper.toUserDetails(userId, signature))
		    .thenReturn(userDetailsRequest);
		
		//invoke
		String userIdFound = service.registerRegulatorInvitedUser(regulatorInvitedUserDetailsDTO, signature);

		assertThat(userIdFound).isEqualTo(userId);

		verify(userSignatureValidatorService, times(1)).validateSignature(signature);
		verify(regulatorInviteUserMapper, times(1)).toUserRepresentation(regulatorInvitedUserDetailsDTO);
		verify(userRegistrationService, times(1)).registerInvitedUser(keycloakUser);
		verify(authService, times(1)).updateUserDetails(userDetailsRequest);
		verify(userDetailsMapper, times(1)).toUserDetails(userId, signature);
	}
	
	@Test
    void registerRegulatorInvitedUser_insert_user_details_fails_should_roolback() throws UserDetailsSaveException {
        String email = "email";
        RegulatorInvitedUserDetailsDTO regulatorInvitedUserDetailsDTO =
                RegulatorInvitedUserDetailsDTO.builder()
                        .email(email).firstName("fn").lastName("ln").jobTitle("jt").phoneNumber("210000").build();
        final String userId = "user";

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setEmail(regulatorInvitedUserDetailsDTO.getEmail());
        keycloakUser.setFirstName(regulatorInvitedUserDetailsDTO.getFirstName());
        keycloakUser.setLastName(regulatorInvitedUserDetailsDTO.getLastName());
        keycloakUser.singleAttribute(KeycloakUserAttributes.PHONE_NUMBER.getName(),
                regulatorInvitedUserDetailsDTO.getPhoneNumber());
        keycloakUser.singleAttribute(KeycloakUserAttributes.JOB_TITLE.getName(),
                regulatorInvitedUserDetailsDTO.getJobTitle());
        
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();

        UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
            .signature(SignatureRequest.builder()
                    .content("content".getBytes())
                    .name("signature")
                    .size(1L)
                    .type("type")
                    .build())
            .build();
        
        when(regulatorInviteUserMapper.toUserRepresentation(regulatorInvitedUserDetailsDTO)).thenReturn(keycloakUser);
        when(userRegistrationService.registerInvitedUser(keycloakUser)).thenReturn(userId);
        when(userDetailsMapper.toUserDetails(userId, signature))
            .thenReturn(userDetailsRequest);
        doThrow(UserDetailsSaveException.class).when(authService).updateUserDetails(userDetailsRequest);
        
        //invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            service.registerRegulatorInvitedUser(regulatorInvitedUserDetailsDTO, signature));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);

        verify(userSignatureValidatorService, times(1)).validateSignature(signature);
        verify(regulatorInviteUserMapper, times(1)).toUserRepresentation(regulatorInvitedUserDetailsDTO);
        verify(userRegistrationService, times(1)).registerInvitedUser(keycloakUser);
        verify(authService, times(1)).updateUserDetails(userDetailsRequest);
        verify(userDetailsMapper, times(1)).toUserDetails(userId, signature);
        verify(authService, times(1)).deleteUser(userId);
    }

	@Test
	void updateRegulatorUser() {
		String userId = "user";
		String username = "username";
		UserRepresentation userRepresentation = createUserRepresentation(userId, "email1", username);
		UserRepresentation userRepresentationUpdated = createUserRepresentation(userId, "email2", username);

		RegulatorUserDTO regulatorUserDTO =
				RegulatorUserDTO.builder().email("email2").firstName("fn").lastName("ln").build();
		
		FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();
		
		UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
		        .signature(SignatureRequest.builder()
		                .content("content".getBytes())
		                .name("signature")
		                .size(1L)
		                .type("type")
		                .build())
		        .build();

		when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
		when(regulatorUserMapper.toUserRepresentation(regulatorUserDTO, userId, userRepresentation.getUsername(),
				userRepresentation.getEmail(), userRepresentation.getAttributes())).thenReturn(userRepresentationUpdated);
		when(userDetailsMapper.toUserDetails(userId, signature))
            .thenReturn(userDetailsRequest);

		//invoke
		service.updateRegulatorUser(userId, regulatorUserDTO, signature);

		verify(userSignatureValidatorService, times(1)).validateSignature(signature);
		verify(authService, times(1)).getUserRepresentationById(userId);
		verify(regulatorUserMapper, times(1)).toUserRepresentation(regulatorUserDTO, userId,
				userRepresentation.getUsername(), userRepresentation.getEmail(), userRepresentation.getAttributes());
		verify(authService, times(1)).updateUser(userRepresentationUpdated);
		verify(userDetailsMapper, times(1)).toUserDetails(userId, signature);
	}
	
	@Test
    void updateRegulatorUser_update_user_details_fails_should_roolback() throws UserDetailsSaveException {
        String userId = "user";
        String username = "username";
        UserRepresentation userRepresentation = createUserRepresentation(userId, "email1", username);
        UserRepresentation userRepresentationUpdated = createUserRepresentation(userId, "email2", username);

        RegulatorUserDTO regulatorUserDTO =
                RegulatorUserDTO.builder().email("email2").firstName("fn").lastName("ln").build();
        
        FileDTO signature = FileDTO.builder()
                .fileContent("content".getBytes())
                .fileName("signature")
                .fileSize(1L)
                .fileType("type")
                .build();
        
        UserDetailsRequest userDetailsRequest = UserDetailsRequest.builder()
                .signature(SignatureRequest.builder()
                        .content("content".getBytes())
                        .name("signature")
                        .size(1L)
                        .type("type")
                        .build())
                .build();

        when(authService.getUserRepresentationById(userId)).thenReturn(userRepresentation);
        when(regulatorUserMapper.toUserRepresentation(regulatorUserDTO, userId, userRepresentation.getUsername(),
                userRepresentation.getEmail(), userRepresentation.getAttributes())).thenReturn(userRepresentationUpdated);
        when(userDetailsMapper.toUserDetails(userId, signature))
            .thenReturn(userDetailsRequest);

        doThrow(UserDetailsSaveException.class).when(authService).updateUserDetails(userDetailsRequest);
        
        //invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            service.updateRegulatorUser(userId, regulatorUserDTO, signature));

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);

        verify(userSignatureValidatorService, times(1)).validateSignature(signature);
        verify(authService, times(1)).getUserRepresentationById(userId);
        verify(regulatorUserMapper, times(1)).toUserRepresentation(regulatorUserDTO, userId,
                userRepresentation.getUsername(), userRepresentation.getEmail(), userRepresentation.getAttributes());
        verify(authService, times(1)).updateUser(userRepresentationUpdated);
        verify(userDetailsMapper, times(1)).toUserDetails(userId, signature);
        verify(authService, times(1)).updateUser(userRepresentation);
    }

	private UserRepresentation createUserRepresentation(String id, String email, String username) {
		UserRepresentation user = new UserRepresentation();
		user.setId(id);
		user.setEmail(email);
		user.setUsername(username);
		return user;
	}

}
