package uk.gov.pmrv.api.web.controller.authorization;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.authorization.core.domain.dto.LoginStatus.ENABLED;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.gov.pmrv.api.authorization.core.service.UserStatusService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith(MockitoExtension.class)
class AuthorityControllerTest {

	private static final String BASE_PATH = "/v1.0/authorities";
	private static final String CURRENT_USER_STATUS_PATH = "current-user-status";
	private static final String USER_ID = "userId";

	private MockMvc mockMvc;

	@InjectMocks
	private AuthorityController authorityController;

	@Mock
	private UserStatusService userStatusService;

	@Mock
	private PmrvSecurityComponent pmrvSecurityComponent;

	@BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authorityController)
				.setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            	.setControllerAdvice(new ExceptionControllerAdvice())
            	.build();
    }

	@Test
	void getCurrentUserStatus() throws Exception {
		PmrvUser currentUser = buildMockPmrvUser(OPERATOR);
		when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(currentUser);
		when(userStatusService.getLoginStatus(currentUser.getUserId())).thenReturn(ENABLED);

		mockMvc.perform(MockMvcRequestBuilders
			.get(BASE_PATH + "/" + CURRENT_USER_STATUS_PATH)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("userId").value(USER_ID))
			.andExpect(jsonPath("roleType").value(String.valueOf(OPERATOR)))
			.andExpect(jsonPath("loginStatus").value(String.valueOf(ENABLED)));
	}

	private PmrvUser buildMockPmrvUser(RoleType role) {
		return PmrvUser.builder().userId(USER_ID).roleType(role).build();
	}
}
