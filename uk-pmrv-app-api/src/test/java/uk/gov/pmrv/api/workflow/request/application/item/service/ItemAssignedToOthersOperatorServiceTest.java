package uk.gov.pmrv.api.workflow.request.application.item.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;

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
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemAssignedToOthersOperatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToOthersOperatorServiceTest {

    @InjectMocks
    private ItemAssignedToOthersOperatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemAssignedToOthersOperatorRepository itemRepository;

    @Mock
    private OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToOthers() {
        final Long accountId = 1L;
        PmrvUser pmrvUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(accountId, Set.of(ACCOUNT_USERS_SETUP));

        Item expectedItem = buildItem(ACCOUNT_USERS_SETUP, "oper2Id");
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L)
                .build();
        ItemDTO expectedItemDTO = buildItemDTO(
                expectedItem,
                UserInfoDTO.builder()
                        .firstName("oper2")
                        .lastName("oper2")
                        .build());
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L)
                .build();

        // Mock
        doReturn(scopedRequestTaskTypes).when(operatorAuthorityResourceAdapter).findUserScopedRequestTaskTypesByAccounts(
            pmrvUser.getUserId(),
            Set.of(accountId));
        doReturn(expectedItemPage).when(itemRepository).findItemsAssignedToOther(pmrvUser.getUserId(),
            scopedRequestTaskTypes, 0L, 10L);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthers(pmrvUser, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
    }

    @Test
    void getItemsAssignedToOthers_no_user_authorities() {
        Long accountId = 1L;
        PmrvUser pmrvUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypesAsString = emptyMap();

        // Mock
        doReturn(scopedRequestTaskTypesAsString).when(operatorAuthorityResourceAdapter).findUserScopedRequestTaskTypesByAccounts(
            pmrvUser.getUserId(),
            Set.of(accountId));

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthers(pmrvUser,0L, 10L);

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemResponseService);

        verify(operatorAuthorityResourceAdapter, times(1))
            .findUserScopedRequestTaskTypesByAccounts(pmrvUser.getUserId(), Set.of(accountId));
    }

    @Test
    void getItemsAssignedToOthersByAccount() {
        Long accountId = 1L;
        PmrvUser pmrvUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(accountId, Set.of(ACCOUNT_USERS_SETUP));

        Item expectedItem = buildItem(ACCOUNT_USERS_SETUP, "oper1Id");
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L)
                .build();
        ItemDTO expectedItemDTO = buildItemDTO(
                expectedItem,
                UserInfoDTO.builder()
                        .firstName("oper1")
                        .lastName("oper1")
                        .build());
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L)
                .build();

        // Mock
        doReturn(scopedRequestTaskTypes).when(operatorAuthorityResourceAdapter).findUserScopedRequestTaskTypesByAccounts(
            pmrvUser.getUserId(),
            Set.of(accountId));

        doReturn(expectedItemPage).when(itemRepository).findItemsAssignedToOther(
            pmrvUser.getUserId(),
            scopedRequestTaskTypes,
            0L,
            10L);

        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthersByAccount(pmrvUser, accountId,0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);
    }

    @Test
    void getItemsAssignedToOthersByAccount_no_user_authorities() {
        Long accountId = 1L;
        PmrvUser pmrvUser = buildOperatorUser("oper1Id", "oper1", "oper1", accountId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypesAsString = emptyMap();

        // Mock
        doReturn(scopedRequestTaskTypesAsString).when(operatorAuthorityResourceAdapter).findUserScopedRequestTaskTypesByAccounts(
            pmrvUser.getUserId(),
            Set.of(accountId));

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToOthersByAccount(pmrvUser, accountId,0L, 10L);

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemResponseService);

        verify(operatorAuthorityResourceAdapter, times(1))
            .findUserScopedRequestTaskTypesByAccounts(pmrvUser.getUserId(), Set.of(accountId));
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.OPERATOR, itemService.getRoleType());
    }

    private PmrvUser buildOperatorUser(String userId, String firstName, String lastName, Long accountId) {
        PmrvAuthority pmrvAuthority = PmrvAuthority.builder()
                .accountId(accountId).build();

        return PmrvUser.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .authorities(List.of(pmrvAuthority))
                .roleType(RoleType.OPERATOR)
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
                .daysRemaining(DAYS.between(LocalDate.now(), item.getTaskDueDate()))
                .account(ItemAccountDTO.builder()
                        .accountId(item.getAccountId())
                        .accountName("accountName")
                        .competentAuthority(ENGLAND)
                        .leName("leName")
                        .build())
                .build();
    }
}
