package uk.gov.pmrv.api.workflow.request.application.item.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemAssignedToMeRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeRegulatorServiceTest {

    @InjectMocks
    private ItemAssignedToMeRegulatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemAssignedToMeRegulatorRepository itemRepository;

    @Mock
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToMe() {
        PmrvUser pmrvUser = buildRegulatorUser("reg1Id", "reg1");
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "reg1Id");
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(expectedItem,
                UserInfoDTO.builder()
                        .firstName("reg1")
                        .lastName("reg1").build());
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();
        pmrvUser.getAuthorities().get(0).getCompetentAuthority();

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRepository).findItemsAssignedTo(pmrvUser.getUserId(),
            scopedRequestTaskTypes, 0L, 10L);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMe(pmrvUser, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(regulatorAuthorityResourceAdapter, times(1))
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, times(1)).findItemsAssignedTo(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L);
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, pmrvUser);
    }

    @Test
    void getItemsAssignedToMe_no_user_authorities() {
        PmrvUser pmrvUser = buildRegulatorUser("reg1Id", "reg1");
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = emptyMap();
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of())
                .totalItems(0L).build();

        // Mock
        doReturn(scopedRequestTaskTypes).when(regulatorAuthorityResourceAdapter).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        doReturn(expectedItemPage).when(itemRepository).findItemsAssignedTo(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
            .getItemsAssignedToMe(pmrvUser, 0L, 10L);

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verify(regulatorAuthorityResourceAdapter, times(1))
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, times(1))
                .findItemsAssignedTo(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L);
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, pmrvUser);
    }

    @Test
    void getItemsAssignedToMeByAccount() {
        final Long accountId = 1L;
        PmrvUser pmrvUser = buildRegulatorUser("reg1Id", "reg1");
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "reg1Id");
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(expectedItem,
                UserInfoDTO.builder()
                        .firstName("reg1")
                        .lastName("reg1").build());
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRepository).findItemsAssignedToByAccount(pmrvUser.getUserId(),
                accountId, scopedRequestTaskTypes, 0L, 10L);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMeByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(regulatorAuthorityResourceAdapter, times(1))
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, times(1)).findItemsAssignedToByAccount(pmrvUser.getUserId(),
                accountId, scopedRequestTaskTypes, 0L, 10L);
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, pmrvUser);
    }

    @Test
    void getItemsAssignedToMeByAccount_no_user_authorities() {
        final Long accountId = 1L;
        PmrvUser pmrvUser = buildRegulatorUser("reg1Id", "reg1");
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = emptyMap();

        // Mock
        doReturn(scopedRequestTaskTypes).when(regulatorAuthorityResourceAdapter).findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
            .getItemsAssignedToMeByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemResponseService);

        verify(regulatorAuthorityResourceAdapter, times(1))
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.REGULATOR, itemService.getRoleType());
    }

    private PmrvUser buildRegulatorUser(String userId, String username) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
                .competentAuthority(ENGLAND)
                .build();

        return PmrvUser.builder()
                .userId(userId)
                .firstName(username)
                .lastName(username)
                .authorities(List.of(pmrvAuthority))
                .roleType(RoleType.REGULATOR)
                .build();
    }

    private Item buildItem(RequestTaskType taskType, String assigneeId) {
        return Item.builder()
                .creationDate(LocalDateTime.now())
                .requestId("1")
                .requestType(RequestType.INSTALLATION_ACCOUNT_OPENING)
                .taskId(1L)
                .taskType(taskType)
                .taskAssigneeId(assigneeId)
                .taskDueDate(LocalDate.of(2021, 1, 1))
                .pauseDate(LocalDate.of(2021, 2, 1))
                .accountId(1L)
                .build();
    }

    private ItemDTO buildItemDTO(Item item, UserInfoDTO taskAssignee) {
        return ItemDTO.builder()
                .creationDate(item.getCreationDate())
                .requestId(item.getRequestId())
                .requestType(item.getRequestType())
                .taskId(item.getTaskId())
                .taskType(item.getTaskType())
                .itemAssignee(ItemAssigneeDTO.builder()
                        .taskAssignee(taskAssignee)
                        .build())
                .daysRemaining(DAYS.between(item.getPauseDate(), item.getTaskDueDate()))
                .account(ItemAccountDTO.builder()
                        .accountId(item.getAccountId())
                        .accountName("accountName")
                        .competentAuthority(ENGLAND)
                        .leName("leName")
                        .build())
                .build();
    }
}
