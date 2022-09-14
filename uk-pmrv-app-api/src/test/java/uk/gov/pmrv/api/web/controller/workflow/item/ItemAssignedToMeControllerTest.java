package uk.gov.pmrv.api.web.controller.workflow.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.pmrv.api.authorization.rules.services.RoleAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.web.config.PmrvUserArgumentResolver;
import uk.gov.pmrv.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.pmrv.api.web.security.AuthorizationAspectUserResolver;
import uk.gov.pmrv.api.web.security.AuthorizedAspect;
import uk.gov.pmrv.api.web.security.AuthorizedRoleAspect;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToMeOperatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToMeRegulatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToMeService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeControllerTest {

    private static final String BASE_PATH = "/v1.0/items";
    private static final String ASSIGNED_TO_ME_PATH = "assigned-to-me";

    private MockMvc mockMvc;

    @Mock
    private ItemAssignedToMeOperatorService itemAssignedToMeOperatorService;

    @Mock
    private ItemAssignedToMeRegulatorService itemAssignedToMeRegulatorService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemAssignedToMeService> services = List.of(itemAssignedToMeOperatorService, itemAssignedToMeRegulatorService);
        ItemAssignedToMeController itemController = new ItemAssignedToMeController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemAssignedToMeController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getAssignedItems_operator() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = buildItemDTOResponse();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToMeOperatorService
                .getItemsAssignedToMe(pmrvUser, 0L, 10L)).thenReturn(itemDTOResponse);
        when(itemAssignedToMeOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
                .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToMeOperatorService, times(1))
                .getItemsAssignedToMe(pmrvUser, 0L, 10L);
        verify(itemAssignedToMeRegulatorService, never())
                .getItemsAssignedToMe(any(), anyLong(), anyLong());
    }

    @Test
    void getAssignedItems_regulator() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = buildItemDTOResponse();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToMeRegulatorService.getItemsAssignedToMe(pmrvUser, 0L, 10L))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToMeOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(itemAssignedToMeRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
                .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToMeOperatorService, never())
                .getItemsAssignedToMe(any(), anyLong(), anyLong());
        verify(itemAssignedToMeRegulatorService, times(1))
                .getItemsAssignedToMe(pmrvUser, 0L, 10L);
    }

    @Test
    void getAssignedItems_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{OPERATOR, REGULATOR, VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemAssignedToMeOperatorService, never())
            .getItemsAssignedToMe(any(), anyLong(), anyLong());
        verify(itemAssignedToMeRegulatorService, never())
            .getItemsAssignedToMe(any(), anyLong(), anyLong());
    }

    @Test
    void getAssignedItemsByAccount_operator() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = buildItemDTOResponse();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToMeOperatorService.getItemsAssignedToMeByAccount(pmrvUser, 2L, 0L, 10L))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToMeOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "/account/2" + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
                .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToMeOperatorService, times(1))
                .getItemsAssignedToMeByAccount(pmrvUser, 2L, 0L, 10L);
        verify(itemAssignedToMeRegulatorService, never())
                .getItemsAssignedToMeByAccount(any(), anyLong(), anyLong(), anyLong());
    }

    @Test
    void getAssignedItemsByAccount_regulator() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = buildItemDTOResponse();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToMeRegulatorService.getItemsAssignedToMeByAccount(pmrvUser, 2L, 0L, 10L))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToMeOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(itemAssignedToMeRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "/account/2" + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
                .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToMeOperatorService, never())
                .getItemsAssignedToMeByAccount(any(), anyLong(), anyLong(), anyLong());
        verify(itemAssignedToMeRegulatorService, times(1))
                .getItemsAssignedToMeByAccount(pmrvUser, 2L, 0L, 10L);
    }

    @Test
    void getAssignedItemsByAccount_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
                .when(pmrvUserAuthorizationService)
                .authorize(pmrvUser, "getAssignedItemsByAccount", "2");

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_ME_PATH + "/account/2" + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(itemAssignedToMeOperatorService, never())
                .getItemsAssignedToMeByAccount(any(), anyLong(), anyLong(), anyLong());
        verify(itemAssignedToMeRegulatorService, never())
                .getItemsAssignedToMeByAccount(any(), anyLong(), anyLong(), anyLong());
    }

    private ItemDTOResponse buildItemDTOResponse() {
        return ItemDTOResponse.builder()
                .items(List.of(buildItemDTO("1", 1L, 1L, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                                UserInfoDTO.builder()
                                        .firstName("reg1")
                                        .lastName("reg1")
                                        .build(),
                               RoleType.REGULATOR),
                        buildItemDTO("2", 2L, 2L, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                                UserInfoDTO.builder()
                                        .firstName("reg1")
                                        .lastName("reg1")
                                        .build(),
                                RoleType.REGULATOR)))
                .totalItems(1L).build();
    }

    private ItemDTO buildItemDTO(String requestId, Long taskId, Long accountId, RequestTaskType taskType,
                                 UserInfoDTO taskAssignee, RoleType roleType) {

        return ItemDTO.builder()
                .creationDate(LocalDateTime.now())
                .requestId(requestId)
                .requestType(RequestType.INSTALLATION_ACCOUNT_OPENING)
                .taskId(taskId)
                .taskType(taskType)
                .itemAssignee(ItemAssigneeDTO.builder()
                        .taskAssignee(taskAssignee)
                        .taskAssigneeType(roleType)
                        .build())
                .daysRemaining(15L)
                .account(ItemAccountDTO.builder()
                        .accountId(accountId)
                        .accountName("accountName")
                        .competentAuthority(ENGLAND)
                        .leName("leName")
                        .build())
                .build();
    }
}
