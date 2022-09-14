package uk.gov.pmrv.api.web.controller.permit;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.service.PermitAttachmentService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

@ExtendWith(MockitoExtension.class)
class PermitControllerTest {
    private static final String PERMIT_CONTROLLER_PATH = "/v1.0/permits";

    private MockMvc mockMvc;

    @InjectMocks
    private PermitController permitController;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private PermitAttachmentService permitAttachmentService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver =
            new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(permitController);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        permitController = (PermitController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(permitController)
            .addFilters(new FilterChainProxy(Collections.emptyList()))
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void getPermitById() throws Exception {
        String id = "1";
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.valueOf(1000))
                .build())
            .build();
        PermitContainer permitContainer = PermitContainer.builder().permit(permit).permitAttachments(Map.of()).build();
        when(permitQueryService.getPermitContainerById(id)).thenReturn(permitContainer);

        mockMvc.perform(MockMvcRequestBuilders.get(PERMIT_CONTROLLER_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.permit.estimatedAnnualEmissions.quantity").value(BigDecimal.valueOf(1000)));
    }

    @Test
    void generateGetPermitAttachmentToken() throws Exception {
        String permitId = "1";
        UUID attachmentUuid = UUID.randomUUID();
        FileToken expectedToken = FileToken.builder().token("token").build();

        when(permitAttachmentService.generateGetFileAttachmentToken(permitId, attachmentUuid)).thenReturn(
            expectedToken);

        mockMvc.perform(MockMvcRequestBuilders
                .get(PERMIT_CONTROLLER_PATH + "/" + permitId + "/attachments")
                .param("uuid", attachmentUuid.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value(expectedToken.getToken()));

        verify(permitAttachmentService, times(1)).generateGetFileAttachmentToken(permitId, attachmentUuid);
    }

    @Test
    void generateGetPermitAttachmentToken_forbidden() throws Exception {
        Long permitId = 1L;
        UUID attachmentUuid = UUID.randomUUID();
        PmrvUser authUser = PmrvUser.builder().userId("userId").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(authUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(authUser, "generateGetPermitAttachmentToken", String.valueOf(permitId));

        mockMvc.perform(MockMvcRequestBuilders
                .get(PERMIT_CONTROLLER_PATH + "/" + permitId + "/attachments")
                .param("uuid", attachmentUuid.toString()))
            .andExpect(status().isForbidden());

        verifyNoInteractions(permitAttachmentService);
    }
}
