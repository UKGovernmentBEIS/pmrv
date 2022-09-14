package uk.gov.pmrv.api.workflow.request.application.item.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.WALES;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.SYSTEM_MESSAGE_NOTIFICATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.RequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemAssignedToOthersRegulatorRepository.class})
class ItemAssignedToOthersRegulatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemAssignedToOthersRegulatorRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItemsAssignedToOthers_no_item_all_assigned_to_me() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        String regulator = "reg1";
        
        Long account =  1L;
        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND);
        createRequestTask(regulator, request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOthers(regulator,
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOthers_no_item_all_operator_tasks() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account =  1L;
        Request request = createRequest(account, SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, ENGLAND);
        createRequestTask("oper1", request, ACCOUNT_USERS_SETUP, "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOthers("reg1",
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOthers_no_item_all_assigned_to_regulator_of_different_ca() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account =  1L;
        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, WALES);
        createRequestTask("reg2", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOthers("reg1",
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOthers_one_item_on_other_regulator() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account =  1L;
        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask =
                createRequestTask("reg2", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request.getCreationDate());

        createOpenedItem(requestTask.getId(), "reg1");

        ItemPage itemPage = itemRepository.findItemsAssignedToOthers("reg1",
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());
        Item item = itemPage.getItems().get(0);
        assertEquals(item.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item.getRequestId(), request.getId());
        assertEquals(item.getRequestType(), request.getType());
        assertEquals(item.getTaskId(), requestTask.getId());
        assertEquals(item.getTaskType(), requestTask.getType());
        assertEquals(item.getTaskAssigneeId(), requestTask.getAssignee());
        assertEquals(item.getTaskDueDate(), requestTask.getDueDate());
        assertEquals(item.getAccountId(), account);
        assertFalse(item.isNew());
    }

    @Test
    void findItemsAssignedToOthers_two_items_on_different_regulators() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account1 =  1L;
        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask("reg2", request1, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());

        Long account2 = 2L;
        Request request2 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND,  LocalDateTime.now().plusDays(1));
        RequestTask requestTask2 =
                createRequestTask("reg3", request2, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", request2.getCreationDate());

        createOpenedItem(requestTask1.getId(), "reg1");
        createOpenedItem(requestTask2.getId(), "reg1");

        ItemPage itemPage = itemRepository.findItemsAssignedToOthers("reg1",
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());
        Item item1 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals(request1.getId()))
                .findFirst().get();
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account1);
        assertFalse(item1.isNew());

        Item item2 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals(request2.getId()))
                .findFirst().get();
        assertEquals(item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item2.getRequestId(), request2.getId());
        assertEquals(item2.getRequestType(), request2.getType());
        assertEquals(item2.getTaskId(), requestTask2.getId());
        assertEquals(item2.getTaskType(), requestTask2.getType());
        assertEquals(item2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(item2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(item2.getAccountId(), account2);
        assertFalse(item2.isNew());
    }

    @Test
    void findItemsAssignedToOthers_no_items_when_no_scoped_request_types() {
        ItemPage itemPage = itemRepository.findItemsAssignedToOthers("reg1",
                Map.of(),
                0L,
                10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).isEmpty();
        assertThat(itemPage.getTotalItems()).isZero();
    }

    @Test
    void findItemsAssignedToOthers_item_of_request_type_system_message_notification() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND,
                    Set.of(
                        INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW
                    )
                );

        Long account1 =  1L;
        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("reg2", request1, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());

        Long account2 =  2L;
        Request request2 = createRequest(account2, SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, ENGLAND,  LocalDateTime.now().plusDays(1));
        createRequestTask("reg3", request2, ACCOUNT_USERS_SETUP, "t2", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOthers("reg1",
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOthers_only_one_item_unauthorized_for_user() {
        Long account = 1L;
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(ENGLAND, Set.of(PERMIT_ISSUANCE_APPLICATION_REVIEW));

        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("reg2", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request.getCreationDate());

        ItemPage itemPage =
            itemRepository.findItemsAssignedToOthers("reg1", scopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOthersByAccount_no_item_all_assigned_to_me() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(
                        INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account =  1L;
        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("reg1", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOthersByAccount("reg1",
                account,
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOthersByAccount_no_item_all_tasks_on_different_accounts() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(
                        INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account1 =  1L;
        Request request = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("reg2", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request.getCreationDate());

        Long account2 =  2L;

        ItemPage itemPage = itemRepository.findItemsAssignedToOthersByAccount("reg1",
                account2,
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOthersByAccount_one_item_on_other_regulator() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(
                        INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account = 1L;
        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask =
                createRequestTask("reg2", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request.getCreationDate());

        createOpenedItem(requestTask.getId(), "reg2");

        ItemPage itemPage = itemRepository.findItemsAssignedToOthersByAccount("reg1",
                account,
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());
        Item item = itemPage.getItems().get(0);
        assertEquals(item.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item.getRequestId(), request.getId());
        assertEquals(item.getRequestType(), request.getType());
        assertEquals(item.getTaskId(), requestTask.getId());
        assertEquals(item.getTaskType(), requestTask.getType());
        assertEquals(item.getTaskAssigneeId(), requestTask.getAssignee());
        assertEquals(item.getTaskDueDate(), requestTask.getDueDate());
        assertEquals(item.getAccountId(), account);
        assertTrue(item.isNew());
    }

    @Test
    void findItemsAssignedToOthersByAccount_two_items_on_different_regulators() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(
                        INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        
        Long account =  1L;

        Request request1 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask("reg2", request1, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());

        Request request2 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, ENGLAND,  LocalDateTime.now().plusDays(1));
        RequestTask requestTask2 =
                createRequestTask("reg3", request2, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", request2.getCreationDate());

        createOpenedItem(requestTask1.getId(), "reg2");
        createOpenedItem(requestTask2.getId(), "reg3");

        ItemPage itemPage = itemRepository.findItemsAssignedToOthersByAccount("reg1",
                account,
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());
        Item item1 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals(request1.getId()))
                .findFirst().get();
        assertEquals(item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item1.getRequestId(), request1.getId());
        assertEquals(item1.getRequestType(), request1.getType());
        assertEquals(item1.getTaskId(), requestTask1.getId());
        assertEquals(item1.getTaskType(), requestTask1.getType());
        assertEquals(item1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(item1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(item1.getAccountId(), account);
        assertTrue(item1.isNew());

        Item item2 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals(request2.getId()))
                .findFirst().get();
        assertEquals(item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(item2.getRequestId(), request2.getId());
        assertEquals(item2.getRequestType(), request2.getType());
        assertEquals(item2.getTaskId(), requestTask2.getId());
        assertEquals(item2.getTaskType(), requestTask2.getType());
        assertEquals(item2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(item2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(item2.getAccountId(), account);
        assertTrue(item2.isNew());
    }

    @Test
    void findItemsAssignedToOthersByAccount_item_of_request_type_system_message_notification() {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(
                        ACCOUNT_USERS_SETUP));
        Long account = 1L;
        Request request = createRequest(account, SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("reg1", request, ACCOUNT_USERS_SETUP, "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOthersByAccount("reg1",
                account,
                scopedRequestTaskTypes,
                0L,
                10L);

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }
    
    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthority ca) {
        Request request = Request.builder()
            .id(RandomStringUtils.random(5))
            .competentAuthority(ca)
            .type(type)
            .status(status)
            .accountId(accountId)
            .build();

        entityManager.persist(request);
        return request;
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthority ca, LocalDateTime creationDate) {
        Request request = createRequest(accountId, type, status, ca);
        request.setCreationDate(creationDate);
        return entityManager.merge(request);
    }

    private RequestTask createRequestTask(String assignee, Request request, RequestTaskType taskType,
            String processTaskId, LocalDateTime startDate) {
        RequestTask requestTask =
                RequestTask.builder()
                        .request(request)
                        .processTaskId(processTaskId)
                        .type(taskType)
                        .assignee(assignee)
                        .startDate(LocalDateTime.now())
                        .dueDate(LocalDate.now().plusMonths(1L))
                        .build();

        entityManager.persist(requestTask);
        requestTask.setStartDate(startDate);

        return requestTask;
    }

    private void createOpenedItem(Long taskId, String userId) {
        RequestTaskVisit requestTaskVisit =
            RequestTaskVisit.builder()
                .taskId(taskId)
                .userId(userId)
                .build();

        entityManager.persist(requestTaskVisit);
    }
}
