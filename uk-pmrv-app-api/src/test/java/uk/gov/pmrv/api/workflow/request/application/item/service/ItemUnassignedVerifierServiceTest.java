package uk.gov.pmrv.api.workflow.request.application.item.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import uk.gov.pmrv.api.authorization.rules.services.resource.VerifierAuthorityResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemUnassignedVerifierRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ItemUnassignedVerifierServiceTest {

    @InjectMocks
    private ItemUnassignedVerifierService service;

    @Mock
    private ItemUnassignedVerifierRepository itemRepository;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private VerifierAuthorityResourceAdapter verifierAuthorityResourceService;


    @Test
    void getUnassignedItems() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();
        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, null);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(expectedItem, null, "accountName", "leName");
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRepository.findUnassignedItems(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, 0L, 10L);

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);

        verify(verifierAuthorityResourceService, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
    }

    @Test
    void getUnassignedItems_empty_scopes() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();

        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();

        // Mock
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, 0L, 10L);

        // Assert
        assertThat(actualResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());

        verify(verifierAuthorityResourceService, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, never()).findUnassignedItems(anyString(), anyMap(), anyLong(), anyLong());
        verify(itemResponseService, never()).toItemDTOResponse(any(), any());
    }

    @Test
    void getUnassignedItems_ReturnsEmptyResponseWhenNoItemsFetched() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();
        ItemPage itemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.emptyItemDTOResponse();

        // Mock
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRepository.findUnassignedItems(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L))
                .thenReturn(itemPage);
        when(itemResponseService.toItemDTOResponse(itemPage, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, 0L, 10L);

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);
    }

    @Test
    void getUnassignedItemsByAccount() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        final Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();
        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, null);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(expectedItem, null, "accountName", "leName");
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRepository.findUnassignedItemsByAccount(pmrvUser.getUserId(), scopedRequestTaskTypes, accountId, 0L, 10L))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItemsByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualResponse);

        verify(verifierAuthorityResourceService, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
    }

    @Test
    void getUnassignedItemsByAccount_empty_scopes() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();
        final Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();

        // Mock
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(pmrvUser.getUserId())).thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItemsByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertThat(actualResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());

        verify(verifierAuthorityResourceService, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, never()).findUnassignedItemsByAccount(anyString(), anyMap(), anyLong(), anyLong(), anyLong());
        verify(itemResponseService, never()).toItemDTOResponse(any(), any());
    }

    @Test
    void getUnassignedItemsByAccount_ReturnsEmptyResponseWhenNoItemsFetched() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        final Long accountId = 1L;
        PmrvUser pmrvUser = PmrvUser.builder().userId("vb1Id").roleType(RoleType.VERIFIER).build();
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.emptyItemDTOResponse();

        // Mock
        when(verifierAuthorityResourceService.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);
        when(itemRepository.findUnassignedItemsByAccount(pmrvUser.getUserId(), scopedRequestTaskTypes, accountId, 0L, 10L))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItemsByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualResponse);

        verify(verifierAuthorityResourceService, times(1))
                .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.VERIFIER, service.getRoleType());
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

    private ItemDTO buildItemDTO(Item item, UserInfoDTO taskAssignee, String accountName, String leName) {
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
                        .accountName(accountName)
                        .competentAuthority(ENGLAND)
                        .leName(leName)
                        .build())
                .build();
    }
}
