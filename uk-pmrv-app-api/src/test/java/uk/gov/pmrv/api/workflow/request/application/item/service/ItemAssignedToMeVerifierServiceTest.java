package uk.gov.pmrv.api.workflow.request.application.item.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemAssignedToMeVerifierRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ItemAssignedToMeVerifierServiceTest {

    @InjectMocks
    private ItemAssignedToMeVerifierService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemAssignedToMeVerifierRepository itemRepository;

    @Mock
    private VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Test
    void getItemsAssignedToMe() {
        final String userId = "vb1Id";
        final Long vbId = 1L;
        final PmrvUser pmrvUser = buildVerifierUser(userId, "vb1", vbId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(vbId, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, userId);
        ItemPage expectedItemPage = ItemPage.builder().items(List.of(expectedItem)).totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(expectedItem,
                UserInfoDTO.builder()
                        .firstName("vb1")
                        .lastName("vb1").build());
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder().items(List.of(expectedItemDTO)).totalItems(1L).build();

        // Mock
        when(verifierAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRepository).findItemsAssignedTo(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMe(pmrvUser, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(verifierAuthorityResourceAdapter, times(1))
                .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, times(1)).findItemsAssignedTo(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L);
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, pmrvUser);
    }

    @Test
    void getItemsAssignedToMe_no_user_authorities() {
        final Long vbId = 1L;
        final PmrvUser pmrvUser = buildVerifierUser("vb1Id", "vb1", vbId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = emptyMap();
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of())
                .totalItems(0L).build();

        // Mock
        doReturn(scopedRequestTaskTypes).when(verifierAuthorityResourceAdapter).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        doReturn(expectedItemPage).when(itemRepository).findItemsAssignedTo(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsAssignedToMe(pmrvUser, 0L, 10L);

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verify(verifierAuthorityResourceAdapter, times(1))
                .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, times(1))
                .findItemsAssignedTo(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L);
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, pmrvUser);
    }

    @Test
    void getItemsAssignedToMeByAccount() {
        final String userId = "vb1Id";
        final Long accountId = 1L;
        final Long vbId = 1L;
        final PmrvUser pmrvUser = buildVerifierUser(userId, "vb1", vbId);
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(vbId, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, userId);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(expectedItem,
                UserInfoDTO.builder()
                        .firstName("vb1")
                        .lastName("vb1").build());
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(verifierAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRepository)
                .findItemsAssignedToByAccount(pmrvUser.getUserId(), accountId, scopedRequestTaskTypes, 0L, 10L);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMeByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(verifierAuthorityResourceAdapter, times(1))
                .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, times(1)).findItemsAssignedToByAccount(pmrvUser.getUserId(),
                accountId, scopedRequestTaskTypes, 0L, 10L);
        verify(itemResponseService, times(1)).toItemDTOResponse(expectedItemPage, pmrvUser);
    }

    @Test
    void getItemsAssignedToMeByAccount_no_user_authorities() {
        final Long accountId = 1L;
        final Long vbId = 1L;
        final PmrvUser pmrvUser = buildVerifierUser("vb1Id", "vb1", vbId);
        Map<CompetentAuthority, Set<String>> scopedRequestTaskTypes = emptyMap();

        // Mock
        doReturn(scopedRequestTaskTypes).when(verifierAuthorityResourceAdapter).findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService
                .getItemsAssignedToMeByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(ItemDTOResponse.emptyItemDTOResponse(), actualItemDTOResponse);

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemResponseService);

        verify(verifierAuthorityResourceAdapter, times(1))
                .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.VERIFIER, itemService.getRoleType());
    }

    private PmrvUser buildVerifierUser(String userId, String username, Long vbId) {
        return PmrvUser.builder()
                .userId(userId)
                .firstName(username)
                .lastName(username)
                .authorities(List.of(PmrvAuthority.builder().verificationBodyId(vbId).build()))
                .roleType(RoleType.VERIFIER)
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
                        .leName("leName")
                        .build())
                .build();
    }
}
