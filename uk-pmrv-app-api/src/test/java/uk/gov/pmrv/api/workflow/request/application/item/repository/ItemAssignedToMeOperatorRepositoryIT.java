package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.NEW_VERIFICATION_BODY_INSTALLATION;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_ISSUANCE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.SYSTEM_MESSAGE_NOTIFICATION;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemAssignedToMeOperatorRepository.class})
class ItemAssignedToMeOperatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemAssignedToMeOperatorRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItemsAssignedTo_request_assignee_false_task_assignee_true() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask =
            createRequestTask("oper1", request, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request.getCreationDate());

        createOpenedItem(requestTask.getId(), "oper1");

        ItemPage itemPage = itemRepository.findItemsAssignedTo("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());
        Item item = itemPage.getItems().get(0);
        assertEquals(requestTask.getStartDate().truncatedTo(ChronoUnit.MILLIS),
            item.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request.getId(), item.getRequestId());
        assertEquals(request.getType(), item.getRequestType());
        assertEquals(requestTask.getId(), item.getTaskId());
        assertEquals(requestTask.getType(), item.getTaskType());
        assertEquals(requestTask.getAssignee(), item.getTaskAssigneeId());
        assertEquals(requestTask.getDueDate(), item.getTaskDueDate());
        assertEquals(account, item.getAccountId());
        assertFalse(item.isNew());
    }

    @Test
    void findItemsAssignedTo_one_request_one_task_single_account() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request = createRequest(account, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        RequestTask requestTask =
            createRequestTask("oper1", request, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedTo("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());

        Item item = itemPage.getItems().get(0);
        assertEquals(requestTask.getStartDate().truncatedTo(ChronoUnit.MILLIS),
            item.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request.getId(), item.getRequestId());
        assertEquals(request.getType(), item.getRequestType());
        assertEquals(requestTask.getId(), item.getTaskId());
        assertEquals(requestTask.getType(), item.getTaskType());
        assertEquals(requestTask.getAssignee(), item.getTaskAssigneeId());
        assertEquals(requestTask.getDueDate(), item.getTaskDueDate());
        assertEquals(account, item.getAccountId());
        assertTrue(item.isNew());
    }

    @Test
    void findItemsAssignedTo_one_task_and_one_system_notification_single_account() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request1 = createRequest(account, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        RequestTask requestTask1 =
            createRequestTask("oper1", request1, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t1", request1.getCreationDate());

        Request request2 = createRequest(account, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        RequestTask requestTask2 =
            createRequestTask("oper1", request2, ACCOUNT_USERS_SETUP, "t2", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedTo("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(2, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        Item item1 = itemPage.getItems().stream()
            .filter(item -> item.getRequestId().equals(request1.getId()))
            .findFirst().get();

        assertEquals(requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS),
            item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request1.getId(), item1.getRequestId());
        assertEquals(request1.getType(), item1.getRequestType());
        assertEquals(requestTask1.getId(), item1.getTaskId());
        assertEquals(requestTask1.getType(), item1.getTaskType());
        assertEquals(requestTask1.getAssignee(), item1.getTaskAssigneeId());
        assertEquals(requestTask1.getDueDate(), item1.getTaskDueDate());
        assertEquals(account, item1.getAccountId());
        assertTrue(item1.isNew());

        Item item2 = itemPage.getItems().stream()
            .filter(item -> item.getRequestId().equals(request2.getId()))
            .findFirst().get();

        assertEquals(requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS),
            item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request2.getId(), item2.getRequestId());
        assertEquals(request2.getType(), item2.getRequestType());
        assertEquals(requestTask2.getId(), item2.getTaskId());
        assertEquals( requestTask2.getType(), item2.getTaskType());
        assertEquals(requestTask2.getAssignee(), item2.getTaskAssigneeId());
        assertEquals(requestTask2.getDueDate(), item2.getTaskDueDate());
        assertEquals(account, item2.getAccountId());
        assertTrue(item2.isNew());
    }

    @Test
    void findItemsAssignedTo_only_one_item_unauthorized_for_user() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request = createRequest(account, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t1", request.getCreationDate());


        ItemPage itemPage = itemRepository.findItemsAssignedTo("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedTo_two_tasks_different_accounts_one_page() {
        Long account1 = 1L;
        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
            createRequestTask("oper1", request1, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Long account2 = 2L;
        Request request2 = createRequest(account2, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        RequestTask requestTask2 =
            createRequestTask("oper1", request2, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t2", request2.getCreationDate());

        createOpenedItem(requestTask1.getId(), "oper1");

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account1, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE),
            account2, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        ItemPage itemPage = itemRepository.findItemsAssignedTo("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(2, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        Item item1 = itemPage.getItems().stream()
            .filter(item -> item.getRequestId().equals(request1.getId()))
            .findFirst().get();

        assertEquals(requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS),
            item1.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request1.getId(), item1.getRequestId());
        assertEquals(request1.getType(), item1.getRequestType());
        assertEquals(requestTask1.getId(), item1.getTaskId());
        assertEquals(requestTask1.getType(), item1.getTaskType());
        assertEquals(requestTask1.getAssignee(), item1.getTaskAssigneeId());
        assertEquals(requestTask1.getDueDate(), item1.getTaskDueDate());
        assertEquals(account1, item1.getAccountId());
        assertFalse(item1.isNew());

        Item item2 = itemPage.getItems().stream()
            .filter(item -> item.getRequestId().equals(request2.getId()))
            .findFirst().get();
        assertEquals(requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS),
            item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request2.getId(), item2.getRequestId());
        assertEquals(request2.getType(), item2.getRequestType());
        assertEquals(requestTask2.getId(), item2.getTaskId());
        assertEquals( requestTask2.getType(), item2.getTaskType());
        assertEquals(requestTask2.getAssignee(), item2.getTaskAssigneeId());
        assertEquals(requestTask2.getDueDate(), item2.getTaskDueDate());
        assertEquals(account2, item2.getAccountId());
        assertTrue(item2.isNew());
    }

    @Test
    void findItemsAssignedTo_two_tasks_different_accounts_two_pages() {
        Long account1 = 1L;
        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("oper1", request1, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Long account2 = 2L;
        Request request2 = createRequest(account2, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request2, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t2", request2.getCreationDate());

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account1, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE),
            account2, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        ItemPage firstPage = itemRepository.findItemsAssignedTo("oper1", userScopedRequestTaskTypes, 0L, 1L);

        assertEquals(2, firstPage.getTotalItems());
        assertEquals(1, firstPage.getItems().size());
        Item firstPageItem = firstPage.getItems().get(0);

        ItemPage secondPage = itemRepository.findItemsAssignedTo("oper1", userScopedRequestTaskTypes, 1L, 1L);
        assertEquals(2, secondPage.getTotalItems());
        assertEquals(1, secondPage.getItems().size());
        Item secondPageItem = secondPage.getItems().get(0);

        assertNotEquals(firstPageItem.getRequestId(), secondPageItem.getRequestId());
        assertNotEquals(firstPageItem.getTaskId(), secondPageItem.getTaskId());
    }

    @Test
    void findItemAssignedTo() {
        Long account1 = 1L;
        Long account2 = 2L;
        Long account3 = 3L;

        String user = "user";
        String anotherUser = "anotherUser";

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account1, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE),
            account2, Set.of(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request2 = createRequest(account1, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask(anotherUser, request2, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t2", request2.getCreationDate());

        Request request3 = createRequest(account1, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask3 =
            createRequestTask(user, request3, ACCOUNT_USERS_SETUP, "t3", request3.getCreationDate());

        Request request4 = createRequest(account1, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask(anotherUser, request4, NEW_VERIFICATION_BODY_INSTALLATION, "t4", request4.getCreationDate());

        Request request5 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask5 =
            createRequestTask(user, request5, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t5", request5.getCreationDate());

        Request request6 = createRequest(account2, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask(user, request6, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t6", request6.getCreationDate());

        Request request8 = createRequest(account3, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask(user, request8, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t8", request8.getCreationDate());

        Request request9 = createRequest(account3, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask9 =
                createRequestTask(user, request9, NEW_VERIFICATION_BODY_INSTALLATION, "t9", request9.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedTo(user, userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(3, itemPage.getTotalItems());
        assertEquals(3, itemPage.getItems().size());

        Optional<Item> optionalItem1 = itemPage.getItems().stream()
            .filter(item -> item.getTaskId().equals(requestTask3.getId()))
            .findFirst();

        Optional<Item> optionalItem2 = itemPage.getItems().stream()
            .filter(item -> item.getTaskId().equals(requestTask5.getId()))
            .findFirst();

        Optional<Item> optionalItem3 = itemPage.getItems().stream()
                .filter(item -> item.getTaskId().equals(requestTask9.getId()))
                .findFirst();

        assertThat(optionalItem1).isNotEmpty();
        assertThat(optionalItem2).isNotEmpty();
        assertThat(optionalItem3).isNotEmpty();

        assertThat(itemPage.getItems()).containsExactlyInAnyOrder(optionalItem1.get(), optionalItem2.get(), optionalItem3.get());
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthority ca, LocalDateTime creationDate) {
        Request request =
            Request.builder()
                .id(RandomStringUtils.random(5))
                .competentAuthority(ca)
                .type(type)
                .status(status)
                .accountId(accountId)
                .creationDate(creationDate)
                .build();

        entityManager.persist(request);

        return request;
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

    private RequestTaskVisit createOpenedItem(Long taskId, String userId) {
        RequestTaskVisit requestTaskVisit =
            RequestTaskVisit.builder()
                .taskId(taskId)
                .userId(userId)
                .build();

        entityManager.persist(requestTaskVisit);

        return requestTaskVisit;
    }
}
