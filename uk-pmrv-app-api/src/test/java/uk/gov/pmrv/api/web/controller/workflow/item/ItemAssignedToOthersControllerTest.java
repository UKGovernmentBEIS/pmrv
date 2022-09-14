package uk.gov.pmrv.api.web.controller.workflow.item;

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

import java.time.LocalDateTime;
import java.util.List;
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
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersOperatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersRegulatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToOthersControllerTest {

    private static final String BASE_PATH = "/v1.0/items";
    private static final String ASSIGNED_TO_OTHERS_PATH = "assigned-to-others";
    private static final String ACCOUNT_PATH = "account";

    private MockMvc mockMvc;

    @Mock
    private ItemAssignedToOthersOperatorService itemAssignedToOthersOperatorService;

    @Mock
    private ItemAssignedToOthersRegulatorService itemAssignedToOthersRegulatorService;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemAssignedToOthersService> services = List.of(itemAssignedToOthersOperatorService, itemAssignedToOthersRegulatorService);
        ItemAssignedToOthersController itemController = new ItemAssignedToOthersController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemAssignedToOthersController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void getItemsAssignedToOthers_operator() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(buildItemDTO("1", 1L, 1L, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                    UserInfoDTO.builder()
                        .firstName("reg2")
                        .lastName("reg2")
                        .build(),
                        RoleType.REGULATOR),
                buildItemDTO("2", 2L, 2L, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                    UserInfoDTO.builder()
                        .firstName("reg3")
                        .lastName("reg3")
                        .build(),
                   RoleType.REGULATOR)))
            .totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToOthersOperatorService.getItemsAssignedToOthers(pmrvUser, 0L, 10L))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
            .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToOthersOperatorService, times(1))
                .getItemsAssignedToOthers(pmrvUser, 0L, 10L);
        verify(itemAssignedToOthersRegulatorService, never())
                .getItemsAssignedToOthers(any(), anyLong(), anyLong());
    }

    @Test
    void getItemsAssignedToOthers_regulator() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(buildItemDTO("1", 1L, 1L, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                        UserInfoDTO.builder()
                                .firstName("reg2")
                                .lastName("reg2")
                                .build(),
                            RoleType.REGULATOR),
                        buildItemDTO("2", 2L, 2L, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                                UserInfoDTO.builder()
                                        .firstName("reg3")
                                        .lastName("reg3")
                                        .build(),
                               RoleType.REGULATOR)))
                .totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToOthersRegulatorService.getItemsAssignedToOthers(pmrvUser, 0L, 10L))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(itemAssignedToOthersRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
                .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToOthersOperatorService, never())
                .getItemsAssignedToOthers(any(), anyLong(), anyLong());
        verify(itemAssignedToOthersRegulatorService, times(1))
                .getItemsAssignedToOthers(pmrvUser, 0L, 10L);
    }

    @Test
    void getItemsAssignedToOthers_forbidden() throws Exception {
        PmrvUser pmrvUser = PmrvUser.builder().build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{OPERATOR, REGULATOR, VERIFIER});

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemAssignedToOthersOperatorService, never())
            .getItemsAssignedToOthers(any(), anyLong(), anyLong());
        verify(itemAssignedToOthersRegulatorService, never())
            .getItemsAssignedToOthers(any(), anyLong(), anyLong());
    }

    @Test
    void getItemsAssignedToOthersByAccount_operator() throws Exception {
        final Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.OPERATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder()
            .items(List.of(buildItemDTO("1", 1L, accountId, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                    UserInfoDTO.builder()
                        .firstName("reg2")
                        .lastName("reg2")
                        .build(),
                        RoleType.REGULATOR),
                buildItemDTO("2", 2L, accountId, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                    UserInfoDTO.builder()
                        .firstName("reg3")
                        .lastName("reg3")
                        .build(),
                     RoleType.REGULATOR)))
            .totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToOthersOperatorService.getItemsAssignedToOthersByAccount(pmrvUser, accountId, 0L, 10L))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "/" + ACCOUNT_PATH + "/" + accountId + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
            .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToOthersOperatorService, times(1))
                .getItemsAssignedToOthersByAccount(pmrvUser, accountId, 0L, 10L);
        verify(itemAssignedToOthersRegulatorService, never())
                .getItemsAssignedToOthersByAccount(any(), anyLong(), anyLong(), anyLong());
    }

    @Test
    void getItemsAssignedToOthersByAccount_regulator() throws Exception {
        final Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).build();
        ItemDTOResponse itemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(buildItemDTO("1", 1L, accountId, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                        UserInfoDTO.builder()
                                .firstName("reg2")
                                .lastName("reg2")
                                .build(),
                          RoleType.REGULATOR),
                        buildItemDTO("2", 2L, accountId, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                                UserInfoDTO.builder()
                                        .firstName("reg3")
                                        .lastName("reg3")
                                        .build(),
                               RoleType.REGULATOR)))
                .totalItems(1L).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemAssignedToOthersRegulatorService.getItemsAssignedToOthersByAccount(pmrvUser, accountId, 0L, 10L))
                .thenReturn(itemDTOResponse);
        when(itemAssignedToOthersOperatorService.getRoleType()).thenReturn(RoleType.OPERATOR);
        when(itemAssignedToOthersRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "/" + ACCOUNT_PATH + "/" + accountId + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()))
                .andExpect(jsonPath("$.items[1].taskId").value(itemDTOResponse.getItems().get(1).getTaskId()));

        verify(itemAssignedToOthersOperatorService, never())
                .getItemsAssignedToOthersByAccount(any(), anyLong(), anyLong(), anyLong());
        verify(itemAssignedToOthersRegulatorService, times(1))
                .getItemsAssignedToOthersByAccount(pmrvUser, accountId, 0L, 10L);
    }

    @Test
    void getItemsAssignedToOthersByAccount_forbidden() throws Exception {
        final long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().roleType(RoleType.REGULATOR).build();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(pmrvUser, "getItemsAssignedToOthersByAccount", Long.toString(accountId));

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + ASSIGNED_TO_OTHERS_PATH + "/" + ACCOUNT_PATH + "/" + accountId + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemAssignedToOthersOperatorService, never())
                .getItemsAssignedToOthersByAccount(any(), anyLong(), anyLong(), anyLong());
        verify(itemAssignedToOthersRegulatorService, never())
                .getItemsAssignedToOthersByAccount(any(), anyLong(), anyLong(), anyLong());
    }

    private ItemDTO buildItemDTO(String requestId, Long taskId, Long accountId, RequestTaskType taskType,
                                 UserInfoDTO taskAssignee, RoleType roleType) {

        return ItemDTO.builder()
            .creationDate(LocalDateTime.now())
            .requestId(requestId)
            .requestType(RequestType.INSTALLATION_ACCOUNT_OPENING)
            .taskId(taskId)
            .taskType(taskType)
            .itemAssignee(ItemAssigneeDTO
                .builder().taskAssignee(taskAssignee)
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
