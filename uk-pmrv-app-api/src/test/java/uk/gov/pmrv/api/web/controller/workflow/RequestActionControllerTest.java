package uk.gov.pmrv.api.web.controller.workflow;

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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.pmrv.api.authorization.rules.services.PmrvUserAuthorizationService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;
import uk.gov.pmrv.api.workflow.request.application.requestaction.RequestActionQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RequestActionControllerTest {
    
    private static final String BASE_PATH = "/v1.0/request-actions";

    private MockMvc mockMvc;

    @InjectMocks
    private RequestActionController controller;
    
    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;
    
    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;
    
    @Mock
    private RequestActionQueryService requestActionQueryService;
    
    @BeforeEach
    void setUp() {
        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(controller);
        aspectJProxyFactory.addAspect(aspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        controller = (RequestActionController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getRequestActionById() throws Exception {
        PmrvUser user = PmrvUser.builder().userId("user").build();
        Long requestActionId = 1L;
        RequestActionDTO requestActionDTO = RequestActionDTO.builder().id(requestActionId)
                .submitter("fn ln").build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        when(requestActionQueryService.getRequestActionById(requestActionId, user)).thenReturn(requestActionDTO);
        mockMvc.perform(
                    MockMvcRequestBuilders.get(BASE_PATH + "/" + requestActionId)
                                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestActionId))
                .andExpect(jsonPath("$.submitter").value("fn ln"));
        
        verify(requestActionQueryService, times(1)).getRequestActionById(requestActionId, user);
    }
    
    @Test
    void getRequestActionById_forbidden() throws Exception {
        PmrvUser user = PmrvUser.builder().userId("user").build();
        Long requestActionId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(user);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(user, "getRequestActionById", String.valueOf(requestActionId));

        mockMvc.perform(
                    MockMvcRequestBuilders.get(BASE_PATH + "/" + requestActionId)
                                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(pmrvSecurityComponent, times(1)).getAuthenticatedUser();
        verify(requestActionQueryService, never()).getRequestActionById(anyLong(), any());
    }

    @Test
    void getRequestActionsByRequestId() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().userId("id").build();

        RequestActionInfoDTO requestActionInfoDTO = RequestActionInfoDTO.builder().id(1L).build();
        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(requestActionQueryService.getRequestActionsByRequestId("2", pmrvUser)).thenReturn(List.of(requestActionInfoDTO));

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH)
                .queryParam("requestId", String.valueOf(2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getRequestActionsByRequestId_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().userId("id").build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(pmrvUser, "getRequestActionsByRequestId", "2");

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH)
                .queryParam("requestId", String.valueOf(2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(requestActionQueryService, never()).getRequestActionsByRequestId(anyString(), any());
    }
}
