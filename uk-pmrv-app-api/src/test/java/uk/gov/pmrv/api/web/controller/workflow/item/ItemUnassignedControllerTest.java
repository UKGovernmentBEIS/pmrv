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
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
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
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemUnassignedRegulatorService;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemUnassignedService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
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
class ItemUnassignedControllerTest {

    private static final String BASE_PATH = "/v1.0/items";
    private static final String UNASSIGNED = "unassigned";

    private static final String USER_ID = "user_id";
    private static final Long PAGE = 0L;
    private static final Long PAGE_SIZE = 10L;

    private MockMvc mockMvc;

    @Mock
    private PmrvSecurityComponent pmrvSecurityComponent;

    @Mock
    private ItemUnassignedRegulatorService itemUnassignedRegulatorService;

    @Mock
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private RoleAuthorizationService roleAuthorizationService;

    @BeforeEach
    public void setUp() {
        List<ItemUnassignedService> services = List.of(itemUnassignedRegulatorService);
        ItemUnassignedController itemController = new ItemUnassignedController(services);

        AuthorizationAspectUserResolver authorizationAspectUserResolver = new AuthorizationAspectUserResolver(pmrvSecurityComponent);
        AuthorizedAspect aspect = new AuthorizedAspect(pmrvUserAuthorizationService, authorizationAspectUserResolver);
        AuthorizedRoleAspect authorizedRoleAspect = new AuthorizedRoleAspect(roleAuthorizationService, authorizationAspectUserResolver);

        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(itemController);
        aspectJProxyFactory.addAspect(aspect);
        aspectJProxyFactory.addAspect(authorizedRoleAspect);

        DefaultAopProxyFactory proxyFactory = new DefaultAopProxyFactory();
        AopProxy aopProxy = proxyFactory.createAopProxy(aspectJProxyFactory);

        itemController = (ItemUnassignedController) aopProxy.getProxy();

        mockMvc = MockMvcBuilders.standaloneSetup(itemController)
            .setCustomArgumentResolvers(new PmrvUserArgumentResolver(pmrvSecurityComponent))
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
    }

    @Test
    void getUnassignedItems_regulator() throws Exception {
        PmrvUser pmrvUser = buildMockPmrvUser(RoleType.REGULATOR);
        ItemDTOResponse itemDTOResponse = buildMockItemDTOResponse();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemUnassignedRegulatorService.getUnassignedItems(pmrvUser, PAGE, PAGE_SIZE))
                .thenReturn(itemDTOResponse);
        when(itemUnassignedRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_PATH + "/" + UNASSIGNED + "?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(itemDTOResponse.getItems().size())))
                .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()));

        verify(itemUnassignedRegulatorService, times(1))
                .getUnassignedItems(pmrvUser, PAGE, PAGE_SIZE);
    }

    @Test
    void getUnassignedItems_forbidden() throws Exception {
        PmrvUser pmrvUser = buildMockPmrvUser(RoleType.REGULATOR);

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(roleAuthorizationService)
            .evaluate(pmrvUser, new RoleType[]{OPERATOR, REGULATOR, VERIFIER});


        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + UNASSIGNED + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemUnassignedRegulatorService, never()).getUnassignedItems(any(), anyLong(), anyLong());
    }

    @Test
    void getUnassignedItemsByAccount_regulator() throws Exception {
        PmrvUser pmrvUser = buildMockPmrvUser(RoleType.REGULATOR);
        Long accountId = 1L;
        ItemDTOResponse itemDTOResponse = buildMockItemDTOResponse();

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        when(itemUnassignedRegulatorService.getUnassignedItemsByAccount(pmrvUser, accountId, PAGE, PAGE_SIZE))
            .thenReturn(itemDTOResponse);
        when(itemUnassignedRegulatorService.getRoleType()).thenReturn(RoleType.REGULATOR);

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + UNASSIGNED + "/account/" + accountId + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items", hasSize(itemDTOResponse.getItems().size())))
            .andExpect(jsonPath("$.items[0].taskId").value(itemDTOResponse.getItems().get(0).getTaskId()));

        verify(itemUnassignedRegulatorService, times(1))
                .getUnassignedItemsByAccount(pmrvUser, accountId, PAGE, PAGE_SIZE);
    }

    @Test
    void getUnassignedItemsByAccount_forbidden() throws Exception {
        PmrvUser pmrvUser = buildMockPmrvUser(RoleType.REGULATOR);
        long accountId = 1L;

        when(pmrvSecurityComponent.getAuthenticatedUser()).thenReturn(pmrvUser);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN))
            .when(pmrvUserAuthorizationService)
            .authorize(pmrvUser, "getUnassignedItemsByAccount", Long.toString(accountId));

        mockMvc.perform(MockMvcRequestBuilders
            .get(BASE_PATH + "/" + UNASSIGNED + "/account/" + accountId + "?page=0&size=10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        verify(itemUnassignedRegulatorService, never()).getUnassignedItemsByAccount(any(), anyLong(), anyLong(), anyLong());
    }

    private PmrvUser buildMockPmrvUser(RoleType roleType) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
            .competentAuthority(ENGLAND)
            .build();

        return PmrvUser.builder()
            .userId(USER_ID)
            .authorities(List.of(pmrvAuthority))
            .roleType(roleType)
            .build();
    }

    private ItemDTOResponse buildMockItemDTOResponse() {
        return ItemDTOResponse.builder()
            .items(List.of(buildMockItemDTO(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, RoleType.REGULATOR)
                    ))
            .totalItems(1L).build();
    }

    private ItemDTO buildMockItemDTO(RequestTaskType taskType, RoleType roleType) {

        UserInfoDTO itemUserInfoDTO = UserInfoDTO.builder().firstName("first").lastName("last").build();

        return ItemDTO.builder()
            .creationDate(LocalDateTime.now())
            .requestId("1")
            .requestType(RequestType.INSTALLATION_ACCOUNT_OPENING)
            .taskId(1L)
            .taskType(taskType)
            .itemAssignee(ItemAssigneeDTO
                .builder().taskAssignee(itemUserInfoDTO)
                .taskAssigneeType(roleType)
                .build())
            .daysRemaining(15L)
            .account(ItemAccountDTO.builder()
                .accountId(1L)
                .accountName("accountName")
                .competentAuthority(ENGLAND)
                .leName("leName")
                .build())
            .build();
    }
}
