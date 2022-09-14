package uk.gov.pmrv.api.workflow.request.application.item.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAssigneeDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemUnassignedRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ItemUnassignedRegulatorServiceTest {

    @InjectMocks
    private ItemUnassignedRegulatorService service;
    
    @Mock
    private ItemUnassignedRegulatorRepository itemUnassignedRegulatorRepository;

    @Mock
    private ItemResponseService itemResponseService;
    
    @Mock
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;


    @Test
    void getUnassignedItems() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        PmrvUser pmrvUser = buildRegulatorUser("reg1");
        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, null);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(
                expectedItem,
                null,
                "accountName",
                ENGLAND,
                "leName");
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);
        when(itemUnassignedRegulatorRepository.findUnassignedItems(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, 0L, 10L);

        // Assert
        assertThat(actualResponse).isEqualTo(expectedItemDTOResponse);
        
        verify(regulatorAuthorityResourceAdapter, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
    }
    
    @Test
    void getUnassignedItems_empty_scopes() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();
        
        PmrvUser pmrvUser = buildRegulatorUser("reg1");

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItems(pmrvUser, 0L, 10L);

        // Assert
        assertThat(actualResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());
        
        verify(regulatorAuthorityResourceAdapter, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemUnassignedRegulatorRepository, never()).findUnassignedItems(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        verify(itemResponseService, never()).toItemDTOResponse(Mockito.any(), Mockito.any());
    }

    @Test
    void getUnassignedItems_ReturnsEmptyResponseWhenNoItemsFetched() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        PmrvUser pmrvUser = buildRegulatorUser("reg1");
        ItemPage itemPage = ItemPage.builder()
                .items(Collections.emptyList())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.emptyItemDTOResponse();

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);
        when(itemUnassignedRegulatorRepository.findUnassignedItems(pmrvUser.getUserId(), scopedRequestTaskTypes, 0L, 10L))
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
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        final Long accountId = 1L;
        PmrvUser pmrvUser = buildRegulatorUser("reg1");
        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, null);
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(
                expectedItem,
                null,
                "accountName",
                ENGLAND,
                "leName");
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);
        when(itemUnassignedRegulatorRepository.findUnassignedItemsByAccount(pmrvUser.getUserId(), scopedRequestTaskTypes, accountId, 0L, 10L))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItemsByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualResponse);
        
        verify(regulatorAuthorityResourceAdapter, times(1))
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
    }

    @Test
    void getUnassignedItemsByAccount_empty_scopes() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();
        final Long accountId = 1L;

        PmrvUser pmrvUser = buildRegulatorUser("reg1");

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItemsByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertThat(actualResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());

        verify(regulatorAuthorityResourceAdapter, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemUnassignedRegulatorRepository, never()).findUnassignedItemsByAccount(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        verify(itemResponseService, never()).toItemDTOResponse(Mockito.any(), Mockito.any());
    }

    @Test
    void getUnassignedItemsByAccount_ReturnsEmptyResponseWhenNoItemsFetched() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        final Long accountId = 1L;
        PmrvUser pmrvUser = buildRegulatorUser("reg1");
        ItemPage expectedItemPage = ItemPage.builder()
                .items(Collections.emptyList())
                .totalItems(0L).build();
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.emptyItemDTOResponse();

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
            .thenReturn(scopedRequestTaskTypes);
        when(itemUnassignedRegulatorRepository.findUnassignedItemsByAccount(pmrvUser.getUserId(), scopedRequestTaskTypes, accountId, 0L, 10L))
                .thenReturn(expectedItemPage);
        when(itemResponseService.toItemDTOResponse(expectedItemPage, pmrvUser))
                .thenReturn(expectedItemDTOResponse);

        // Invoke
        ItemDTOResponse actualResponse = service.getUnassignedItemsByAccount(pmrvUser, accountId, 0L, 10L);

        // Assert
        assertEquals(expectedItemDTOResponse, actualResponse);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.REGULATOR, service.getRoleType());
    }

    private PmrvUser buildRegulatorUser(String userId) {
        return PmrvUser.builder()
                .userId(userId)
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
                .accountId(1L)
                .build();
    }

    private ItemDTO buildItemDTO(Item item, UserInfoDTO taskAssignee, String accountName, CompetentAuthority ca,
                                 String leName) {
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
                        .competentAuthority(ca)
                        .leName(leName)
                        .build())
                .build();
    }
}
