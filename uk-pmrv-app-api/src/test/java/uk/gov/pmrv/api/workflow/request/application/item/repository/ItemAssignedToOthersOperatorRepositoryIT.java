package uk.gov.pmrv.api.workflow.request.application.item.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_ISSUANCE;
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
@Import({ObjectMapper.class, ItemAssignedToOthersOperatorRepository.class})
class ItemAssignedToOthersOperatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemAssignedToOthersOperatorRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItemsAssignedToOther_no_items_on_others() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        Request request2 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request2, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOther_one_task_on_other_operator() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account,
            Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request = createRequest(account, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask =
                createRequestTask("oper2", request, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t1", request.getCreationDate());

        createOpenedItem(requestTask.getId(), "oper1");

        ItemPage itemPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1L, itemPage.getItems().size());
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
    void findItemsAssignedToOther_two_items_on_other_operator() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request1 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask("oper2", request1, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Request request2 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        RequestTask requestTask2 =
                createRequestTask("oper2", request2, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t2", request2.getCreationDate());

        createOpenedItem(requestTask1.getId(), "oper1");
        createOpenedItem(requestTask2.getId(), "oper1");

        ItemPage itemPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(2L, itemPage.getTotalItems());
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
        assertFalse(item1.isNew());

        Item item2 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals(request2.getId()))
                .findFirst().get();
        assertEquals(requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS),
                item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request2.getId(), item2.getRequestId());
        assertEquals(request2.getType(), item2.getRequestType());
        assertEquals(requestTask2.getId(), item2.getTaskId());
        assertEquals(requestTask2.getType(), item2.getTaskType());
        assertEquals(requestTask2.getAssignee(), item2.getTaskAssigneeId());
        assertEquals(requestTask2.getDueDate(), item2.getTaskDueDate());
        assertEquals(account, item2.getAccountId());
        assertFalse(item2.isNew());
    }

    @Test
    void findItemsAssignedToOther_two_items_on_different_operators() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request1 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask("oper2", request1, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Request request2 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        RequestTask requestTask2 =
                createRequestTask("oper3", request2, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t2", request2.getCreationDate());

        createOpenedItem(requestTask1.getId(), "oper2");
        createOpenedItem(requestTask2.getId(), "oper3");

        ItemPage itemPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(2L, itemPage.getTotalItems());
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
        assertEquals(requestTask2.getType(), item2.getTaskType());
        assertEquals(requestTask2.getAssignee(), item2.getTaskAssigneeId());
        assertEquals(requestTask2.getDueDate(), item2.getTaskDueDate());
        assertEquals(account, item2.getAccountId());
        assertTrue(item2.isNew());
    }

    @Test
    void findItemsAssignedToOther_two_items_on_different_operators_two_accounts() {
        Long account1 = 1L;
        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask("oper2", request1, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Long account2 = 2L;
        Request request2 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        RequestTask requestTask2 =
                createRequestTask("oper3", request2, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t2", request2.getCreationDate());

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account1, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE),
            account2, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        ItemPage itemPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(2L, itemPage.getTotalItems());
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
        assertTrue(item1.isNew());

        Item item2 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals(request2.getId()))
                .findFirst().get();
        assertEquals(requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS),
                item2.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request2.getId(), item2.getRequestId());
        assertEquals(request2.getType(), item2.getRequestType());
        assertEquals(requestTask2.getId(), item2.getTaskId());
        assertEquals(requestTask2.getType(), item2.getTaskType());
        assertEquals(requestTask2.getAssignee(), item2.getTaskAssigneeId());
        assertEquals(requestTask2.getDueDate(), item2.getTaskDueDate());
        assertEquals(account2, item2.getAccountId());
        assertTrue(item2.isNew());
    }

    @Test
    void findItemsAssignedToOther_two_items_on_different_operators_two_accounts_two_pages() {
        Long account1 = 1L;
        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("oper2", request1, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Long account2 = 2L;
        Request request2 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper3", request2, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t2", request2.getCreationDate());

        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account1, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE),
            account2, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        ItemPage firstPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 1L);

        assertEquals(2, firstPage.getTotalItems());
        assertEquals(1, firstPage.getItems().size());
        Item firstPageItem = firstPage.getItems().get(0);

        ItemPage secondPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 1L, 1L);

        assertEquals(2, secondPage.getTotalItems());
        assertEquals(1, secondPage.getItems().size());
        Item secondPageItem = secondPage.getItems().get(0);

        assertNotEquals(firstPageItem.getRequestId(), secondPageItem.getRequestId());
        assertNotEquals(firstPageItem.getTaskId(), secondPageItem.getTaskId());

    }

    @Test
    void findItemsAssignedToOther_item_of_request_type_system_message_notification() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(PERMIT_ISSUANCE_APPLICATION_SUBMIT, INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request1 = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("oper2", request1, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request1.getCreationDate());

        Request request2 = createRequest(account, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper3", request2, ACCOUNT_USERS_SETUP, "t2", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(1, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToOther_only_one_item_unauthorized_for_user() {
        Long account = 1L;
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = Map.of(
            account, Set.of(INSTALLATION_ACCOUNT_OPENING_ARCHIVE)
        );

        Request request = createRequest(account, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper2", request, PERMIT_ISSUANCE_APPLICATION_SUBMIT, "t1", request.getCreationDate());


        ItemPage itemPage = itemRepository.findItemsAssignedToOther("oper1", userScopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
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

    private void createOpenedItem(Long taskId, String userId) {
        RequestTaskVisit requestTaskVisit =
            RequestTaskVisit.builder()
                .taskId(taskId)
                .userId(userId)
                .build();

        entityManager.persist(requestTaskVisit);
    }
}
