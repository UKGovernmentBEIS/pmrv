package uk.gov.pmrv.api.web.controller.user;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.dto.OneTimePasswordDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.service.UserSecuritySetupService;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith({MockitoExtension.class})
class UserSecuritySetupControllerTest {

    private static final String BASE_PATH = "/v1.0/users/security-setup";
    private static final String REQUEST_TO_CHANGE_2FA_PATH = "/2fa/request-change";
    private static final String CHANGE_2FA_PATH = "/2fa/delete";

    @InjectMocks
    private UserSecuritySetupController userSecuritySetupController;

    @Mock
    private UserSecuritySetupService userSecuritySetupService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private Validator validator;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userSecuritySetupController)
            .setValidator(validator)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        objectMapper = new ObjectMapper();
    }

    @Test
    void requestTwoFactorAuthChange() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();
        String otp = "otp";
        String accessToken = "accessToken";

        OneTimePasswordDTO otpDTO = OneTimePasswordDTO.builder().password(otp).build();

        when(pmrvSecurityComponent.getAccessToken()).thenReturn(accessToken);
        doNothing().when(userSecuritySetupService).requestTwoFactorAuthChange(pmrvUser, accessToken, otp);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + REQUEST_TO_CHANGE_2FA_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(otpDTO)))
            .andExpect(status().isNoContent());

    }

    @Test
    void deleteOtpCredentials() throws Exception {
        TokenDTO token = TokenDTO.builder().token("token").build();

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH + CHANGE_2FA_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(token)))
                .andExpect(status().isNoContent());

        verify(userSecuritySetupService, times(1)).deleteOtpCredentials(token);
    }
}