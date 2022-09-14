package uk.gov.pmrv.api.web.controller.user;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.NONE;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInvitationService;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith(MockitoExtension.class)
class RegulatorUserInvitationControllerTest {

    public static final String BASE_PATH = "/v1.0/regulator-users/invite";

    private MockMvc mockMvc;

    @InjectMocks
    private RegulatorUserInvitationController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private RegulatorUserInvitationService regulatorUserInvitationService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;
    
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (RegulatorUserInvitationController) aopProxy.getProxy();
    	objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }

	@Test
    void inviteRegulatorUserToCA() throws Exception {
		PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
		RegulatorInvitedUserDTO invitedUser = createInvitedUser();
		MockMultipartFile regulatorInvitedUserRequestPart = new MockMultipartFile(
                "regulatorInvitedUser", 
                "",
                MimeTypeUtils.APPLICATION_JSON_VALUE, 
                objectMapper.writeValueAsString(invitedUser).getBytes());
		
		String originalFilename = "filename.txt";
        String contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        byte[] fileContent = "content".getBytes();
        MockMultipartFile signatureRequestPart = new MockMultipartFile("signature", originalFilename, contentType, fileContent);

    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
    	mockMvc
    	    .perform(MockMvcRequestBuilders.multipart(BASE_PATH)
                .file(signatureRequestPart)
                .file(regulatorInvitedUserRequestPart))
    	    .andExpect(status().isNoContent());
    
    	FileDTO expectedSignatureDTO = FileDTO.builder()
                .fileName(originalFilename)
                .fileType(contentType)
                .fileContent(fileContent)
                .fileSize(fileContent.length)
                .build();
	    verify(regulatorUserInvitationService, times(1)).inviteRegulatorUser(invitedUser, expectedSignatureDTO, currentUser);
    }

	@Test
	void inviteRegulatorUserToCA_bad_request_when_invited_user_not_valid() throws Exception {
		PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
		RegulatorInvitedUserDTO invitedUser = RegulatorInvitedUserDTO.builder().build();
		MockMultipartFile regulatorInvitedUserRequestPart = new MockMultipartFile(
                "regulatorInvitedUser", 
                "",
                MimeTypeUtils.APPLICATION_JSON_VALUE, 
                objectMapper.writeValueAsString(invitedUser).getBytes());

		String originalFilename = "filename.txt";
        String contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        byte[] fileContent = "content".getBytes();
        MockMultipartFile signatureRequestPart = new MockMultipartFile("signature", originalFilename, contentType, fileContent);
        
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

		//invoke
        mockMvc
            .perform(MockMvcRequestBuilders.multipart(BASE_PATH)
                .file(signatureRequestPart)
                .file(regulatorInvitedUserRequestPart))
			.andExpect(status().isBadRequest());

		verifyNoInteractions(regulatorUserInvitationService);
	}
	
    @Test
    void inviteRegulatorUserToCA_forbidden() throws Exception {
    	PmrvUser currentUser = PmrvUser.builder().userId("currentuser").build();
    	RegulatorInvitedUserDTO invitedUser = createInvitedUser();
    	MockMultipartFile regulatorInvitedUserRequestPart = new MockMultipartFile(
                "regulatorInvitedUser", 
                "",
                MimeTypeUtils.APPLICATION_JSON_VALUE, 
                objectMapper.writeValueAsString(invitedUser).getBytes());
    	
        String originalFilename = "filename.txt";
        String contentType = MimeTypeUtils.TEXT_PLAIN_VALUE;
        byte[] fileContent = "content".getBytes();
        MockMultipartFile signatureRequestPart = new MockMultipartFile("signature", originalFilename, contentType, fileContent);
    	
    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(currentUser, "inviteRegulatorUserToCA");
    	
        //invoke
        mockMvc
        .perform(MockMvcRequestBuilders.multipart(BASE_PATH)
            .file(signatureRequestPart)
            .file(regulatorInvitedUserRequestPart))
        	.andExpect(status().isForbidden());
    
        verifyNoInteractions(regulatorUserInvitationService);
    }

	private RegulatorInvitedUserDTO createInvitedUser() {
		RegulatorInvitedUserDTO invitedUser = 
				RegulatorInvitedUserDTO.builder()
					.userDetails(RegulatorInvitedUserDetailsDTO.builder()
									.firstName("fn")
									.lastName("ln")
									.email("em@em.gr")
									.jobTitle("title")
									.phoneNumber("210000")
									.build())
					.permissions(Map.of(MANAGE_USERS_AND_CONTACTS, NONE))
					.build();
		return invitedUser;
	}

}
