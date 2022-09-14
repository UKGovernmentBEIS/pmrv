package uk.gov.pmrv.api.web.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityDeletionService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.orchestrator.VerifierUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.VerifierUserAuthorityUpdateOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.dto.UserAuthorityInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.UsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityControllerTest {
	
	public static final String BASE_PATH = "/v1.0/verifier-authorities";

	private MockMvc mockMvc;

    @InjectMocks
    private VerifierAuthorityController controller;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

	@Mock
    private VerifierUserAuthorityUpdateOrchestrator verifierUserAuthorityUpdateOrchestrator;

    @Mock
    private VerifierUserAuthorityQueryOrchestrator verifierUserAuthorityQueryOrchestrator;

	@Mock
	private VerifierAuthorityDeletionService verifierAuthorityDeletionService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @Mock
    private Validator validator;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    public void setUp() {
    	objectMapper = new ObjectMapper();
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (VerifierAuthorityController) aopProxy.getProxy();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
				.setValidator(validator)
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }
    
    @Test
    void getVerifierAuthorities() throws Exception {
    	PmrvUser user = PmrvUser.builder().userId("currentuser").build();
        UserAuthorityInfoDTO verifierUserAuthorityInfoDTO = UserAuthorityInfoDTO.builder()
    			.userId("user")
    			.firstName("fn")
    			.lastName("ln")
    			.roleName("Verifier admin")
    			.build();
    	
    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(verifierUserAuthorityQueryOrchestrator.getVerifierUsersAuthoritiesInfo(user))
        	.thenReturn(UsersAuthoritiesInfoDTO.builder()
        			.authorities(List.of(verifierUserAuthorityInfoDTO))
        			.editable(true)
        			.build());
        
        // Invoke
        mockMvc.perform(
        		MockMvcRequestBuilders.get(BASE_PATH)
        							.contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authorities[0].userId").value(verifierUserAuthorityInfoDTO.getUserId()))
            .andExpect(jsonPath("$.editable").value(Boolean.TRUE));

	    verify(verifierUserAuthorityQueryOrchestrator, times(1)).getVerifierUsersAuthoritiesInfo(user);
    }
    
    @Test
    void getVerifierAuthorities_forbidden() throws Exception {
    	PmrvUser user = PmrvUser.builder().userId("currentuser").build();

    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
	        .when(roleAuthorizationService)
	        .evaluate(user, new RoleType[] {RoleType.VERIFIER});
        
        mockMvc.perform(
	        		MockMvcRequestBuilders.get(BASE_PATH)
	        							.contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isForbidden());
          
	    verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
	    verifyNoInteractions(verifierUserAuthorityQueryOrchestrator);
    }

	@Test
	void getVerifierAuthoritiesByVerificationBodyId() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("userId").roleType(RoleType.REGULATOR).build();
		final Long vbId = 1L;

        List<UserAuthorityInfoDTO> verifierUserAuthorities = List.of(
            UserAuthorityInfoDTO.builder().userId("user1").firstName("fn1").lastName("ln1").roleName("role1").build(),
            UserAuthorityInfoDTO.builder().userId("user2").firstName("fn2").lastName("ln2").roleName("role2").build()
        );
        UsersAuthoritiesInfoDTO verifierUsersAuthoritiesInfo = UsersAuthoritiesInfoDTO.builder()
            .authorities(verifierUserAuthorities)
            .editable(true)
            .build();

		// Mock
		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		when(verifierUserAuthorityQueryOrchestrator.getVerifierAuthoritiesByVerificationBodyId(vbId)).thenReturn(verifierUsersAuthoritiesInfo);

		// Invoke
		mockMvc.perform(
				MockMvcRequestBuilders.get(BASE_PATH + "/vb/" + vbId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
                .andExpect(jsonPath("$.authorities[0].userId").value(verifierUserAuthorities.get(0).getUserId()))
				.andExpect(jsonPath("$.authorities[0].firstName").value(verifierUserAuthorities.get(0).getFirstName()))
				.andExpect(jsonPath("$.authorities[0].lastName").value(verifierUserAuthorities.get(0).getLastName()))
				.andExpect(jsonPath("$.authorities[0].roleName").value(verifierUserAuthorities.get(0).getRoleName()))
                .andExpect(jsonPath("$.authorities[1].userId").value(verifierUserAuthorities.get(1).getUserId()))
				.andExpect(jsonPath("$.authorities[1].firstName").value(verifierUserAuthorities.get(1).getFirstName()))
				.andExpect(jsonPath("$.authorities[1].lastName").value(verifierUserAuthorities.get(1).getLastName()))
				.andExpect(jsonPath("$.authorities[1].roleName").value(verifierUserAuthorities.get(1).getRoleName()))
                .andExpect(jsonPath("$.editable").value(Boolean.TRUE));

		verify(verifierUserAuthorityQueryOrchestrator, times(1)).getVerifierAuthoritiesByVerificationBodyId(vbId);
	}

	@Test
	void getVerifierAuthoritiesByVerificationBodyId_forbidden() throws Exception {
		PmrvUser user = PmrvUser.builder().userId("userId").roleType(RoleType.REGULATOR).build();

		// Mock
		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN))
				.when(pmrvUserAuthorizationService)
				.authorize(user, "getVerifierAuthoritiesByVerificationBodyId");

		// Invoke
		mockMvc.perform(
				MockMvcRequestBuilders.get(BASE_PATH + "/vb/" + 1)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
		verify(verifierUserAuthorityQueryOrchestrator, never()).getVerifierAuthoritiesByVerificationBodyId(anyLong());
	}

    @Test
    void updateVerifierAuthorities() throws Exception {
    	Long verificationBodyId = 1L;
    	PmrvUser currentUser =
    			PmrvUser.builder()
    				.userId("currentuser")
    				.roleType(RoleType.VERIFIER)
    				.authorities(List.of(PmrvAuthority.builder().code("code1").verificationBodyId(verificationBodyId).build()))
    				.build();
    	List<VerifierAuthorityUpdateDTO> verifiersUpdate =
			List.of(
					VerifierAuthorityUpdateDTO.builder().authorityStatus(AuthorityStatus.ACTIVE).roleCode("roleCode1").userId("user1").build()
					);

    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
        		MockMvcRequestBuilders.post(BASE_PATH)
        							.contentType(MediaType.APPLICATION_JSON)
        							.content(objectMapper.writeValueAsString(verifiersUpdate)))
        		.andExpect(status().isNoContent());
    
	    verify(verifierUserAuthorityUpdateOrchestrator, times(1))
				.updateVerifierAuthorities(verifiersUpdate, verificationBodyId);
    }
    
    @Test
    void updateVerifierAuthorities_forbidden() throws Exception {
    	Long verificationBodyId = 1L;
    	PmrvUser currentUser =
    			PmrvUser.builder()
    				.userId("currentuser")
    				.roleType(RoleType.VERIFIER)
    				.authorities(List.of(PmrvAuthority.builder().code("code1").verificationBodyId(verificationBodyId).build()))
    				.build();
    	List<VerifierAuthorityUpdateDTO> verifiersUpdate =
			List.of(
					VerifierAuthorityUpdateDTO.builder().authorityStatus(AuthorityStatus.ACTIVE).roleCode("roleCode1").userId("user1").build()
					);

    	when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
    	doThrow(new BusinessException(ErrorCode.FORBIDDEN))
	    	.when(pmrvUserAuthorizationService)
			.authorize(currentUser, "updateVerifierAuthorities");

        //invoke
        mockMvc.perform(
        		MockMvcRequestBuilders.post(BASE_PATH)
        							.contentType(MediaType.APPLICATION_JSON)
        							.content(objectMapper.writeValueAsString(verifiersUpdate)))
        .andExpect(status().isForbidden());
    
	    verify(verifierUserAuthorityUpdateOrchestrator, never()).updateVerifierAuthorities(Mockito.any(), Mockito.any());
    }

    @Test
    void updateVerifierAuthoritiesById() throws Exception {
        Long vbId = 1L;
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").roleType(RoleType.REGULATOR).build();
        List<VerifierAuthorityUpdateDTO> verifiersUpdate =
            List.of(
                VerifierAuthorityUpdateDTO.builder().authorityStatus(AuthorityStatus.ACTIVE).roleCode("roleCode1").userId("user1").build()
            );

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH + "/vb/" + vbId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifiersUpdate)))
            .andExpect(status().isNoContent());

        verify(verifierUserAuthorityUpdateOrchestrator, times(1)).updateVerifierAuthorities(verifiersUpdate, vbId);
    }

    @Test
    void updateVerifierAuthoritiesById_forbidden() throws Exception {
        PmrvUser currentUser = PmrvUser.builder().userId("currentuser").roleType(RoleType.VERIFIER).build();
        List<VerifierAuthorityUpdateDTO> verifiersUpdate =
            List.of(
                VerifierAuthorityUpdateDTO.builder().authorityStatus(AuthorityStatus.ACTIVE).roleCode("roleCode1").userId("user1").build()
            );

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(currentUser, "updateVerifierAuthoritiesByVerificationBodyId");

        //invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH + "/vb/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifiersUpdate)))
            .andExpect(status().isForbidden());

        verifyNoInteractions(verifierUserAuthorityUpdateOrchestrator);
    }

	@Test
	void deleteVerifierAuthority() throws Exception {
		PmrvUser authUser = PmrvUser.builder().userId("currentuser").build();
		String userIdToBeDeleted = "userId";

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
		// Invoke
		mockMvc.perform(
			MockMvcRequestBuilders.delete(BASE_PATH + "/" + userIdToBeDeleted)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(verifierAuthorityDeletionService, times(1)).deleteVerifierAuthority(userIdToBeDeleted, authUser);
	}

	@Test
	void deleteVerifierAuthority_forbidden() throws Exception {
		PmrvUser authUser = PmrvUser.builder().userId("currentuser")
			.authorities(
				List.of(PmrvAuthority.builder().verificationBodyId(1L).build()))
			.build();
		String userIdToBeDeleted = "userId";

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN))
			.when(pmrvUserAuthorizationService)
			.authorize(authUser, "deleteVerifierAuthority");

		// Invoke
		mockMvc.perform(
			MockMvcRequestBuilders.delete(BASE_PATH + "/" + userIdToBeDeleted)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());

		verify(verifierAuthorityDeletionService, never()).deleteVerifierAuthority(anyString(), anyLong());
	}

	@Test
	void deleteCurrentVerifierAuthority() throws Exception {
		PmrvUser authUser = PmrvUser.builder().userId("currentuser")
			.authorities(
				List.of(PmrvAuthority.builder().verificationBodyId(1L).build()))
			.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
		// Invoke
		mockMvc.perform(
			MockMvcRequestBuilders.delete(BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());

		verify(verifierAuthorityDeletionService, times(1)).deleteVerifierAuthority(authUser.getUserId(), authUser.getVerificationBodyId());
	}

	@Test
	void deleteCurrentVerifierAuthority_forbidden() throws Exception {
		PmrvUser authUser = PmrvUser.builder().userId("currentuser")
			.authorities(
				List.of(PmrvAuthority.builder().verificationBodyId(1L).build()))
			.build();

		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
		doThrow(new BusinessException(ErrorCode.FORBIDDEN))
			.when(pmrvUserAuthorizationService)
			.authorize(authUser, "deleteCurrentVerifierAuthority");

		// Invoke
		mockMvc.perform(
			MockMvcRequestBuilders.delete(BASE_PATH)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());

		verify(verifierAuthorityDeletionService, never()).deleteVerifierAuthority(anyString(), anyLong());
	}
}
