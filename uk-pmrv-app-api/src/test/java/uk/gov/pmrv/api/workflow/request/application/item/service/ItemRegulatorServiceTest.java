package uk.gov.pmrv.api.workflow.request.application.item.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
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
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@ExtendWith(MockitoExtension.class)
class ItemRegulatorServiceTest {

    @InjectMocks
    private ItemRegulatorService itemService;

    @Mock
    private ItemResponseService itemResponseService;

    @Mock
    private ItemRegulatorRepository itemRepository;

    @Mock
    private RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    @Test
    void getItemsByRequest() {
        final String requestId = "1";
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        PmrvUser pmrvUser = buildRegulatorUser("reg1Id");
        Item expectedItem = buildItem(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "reg2Id");
        ItemPage expectedItemPage = ItemPage.builder()
                .items(List.of(expectedItem))
                .totalItems(1L).build();
        ItemDTO expectedItemDTO = buildItemDTO(
                expectedItem,
                UserInfoDTO.builder()
                        .firstName("reg2")
                        .lastName("reg2").build(),
                "accountName",
                ENGLAND,
                "leName");
        ItemDTOResponse expectedItemDTOResponse = ItemDTOResponse.builder()
                .items(List.of(expectedItemDTO))
                .totalItems(1L).build();

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);
        doReturn(expectedItemPage).when(itemRepository).findItemsByRequestId(scopedRequestTaskTypes, requestId);
        doReturn(expectedItemDTOResponse).when(itemResponseService).toItemDTOResponse(expectedItemPage, pmrvUser);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsByRequest(pmrvUser, requestId);

        // Assert
        assertEquals(expectedItemDTOResponse, actualItemDTOResponse);

        verify(regulatorAuthorityResourceAdapter, times(1))
                .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, times(1))
                .findItemsByRequestId(scopedRequestTaskTypes, requestId);
        verify(itemResponseService, times(1))
                .toItemDTOResponse(expectedItemPage, pmrvUser);
    }

    @Test
    void getItemsByRequest_empty_scopes() {
        final String requestId = "1";
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of();
        PmrvUser pmrvUser = buildRegulatorUser("reg1Id");

        // Mock
        when(regulatorAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId()))
                .thenReturn(scopedRequestTaskTypes);

        // Invoke
        ItemDTOResponse actualItemDTOResponse = itemService.getItemsByRequest(pmrvUser, requestId);

        // Assert
        assertThat(actualItemDTOResponse).isEqualTo(ItemDTOResponse.emptyItemDTOResponse());

        verify(regulatorAuthorityResourceAdapter, times(1)).findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        verify(itemRepository, never()).findItemsByRequestId(Mockito.anyMap(), Mockito.anyString());
        verify(itemResponseService, never()).toItemDTOResponse(Mockito.any(), Mockito.any());
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.REGULATOR, itemService.getRoleType());
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
