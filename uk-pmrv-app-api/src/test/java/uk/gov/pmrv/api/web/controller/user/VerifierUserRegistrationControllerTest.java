package uk.gov.pmrv.api.web.controller.user;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.validation.Validator;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserAcceptInvitationService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;

@ExtendWith(MockitoExtension.class)
class VerifierUserRegistrationControllerTest {

    public static final String BASE_PATH = "/v1.0/verifier-users/registration";
    public static final String ENABLE_FROM_INVITATION = "/enable-from-invitation";

    private MockMvc mockMvc;

    @InjectMocks
    private VerifierUserRegistrationController controller;

    @Mock
    private VerifierUserInvitationService verifierUserInvitationService;

    @Mock
    private VerifierUserAcceptInvitationService verifierUserAcceptInvitationService;

    @Mock
    private Validator validator;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setValidator(validator)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void acceptVerifierInvitation() throws Exception {
        String email = "email";
        TokenDTO tokenDTO = TokenDTO.builder().token("token").build();
        InvitedUserInfoDTO invitedUserInfo = InvitedUserInfoDTO.builder().email(email).build();

        // Mock
        when(verifierUserInvitationService.acceptInvitation(tokenDTO.getToken())).thenReturn(invitedUserInfo);

        // Invoke
        mockMvc.perform(
            MockMvcRequestBuilders.post(BASE_PATH + "/accept-invitation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tokenDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void acceptAndEnableVerifierInvitedUser() throws Exception {
        InvitedUserEnableDTO invitedUserEnableDTO = InvitedUserEnableDTO.builder()
            .invitationToken("token")
            .password("password")
            .build();

        // Invoke
        mockMvc.perform(
            MockMvcRequestBuilders.put(BASE_PATH + ENABLE_FROM_INVITATION)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invitedUserEnableDTO)))
            .andExpect(status().isNoContent());

        verify(verifierUserAcceptInvitationService, times(1)).acceptAndEnableVerifierInvitedUser(invitedUserEnableDTO);
    }
}
