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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.NEW_VERIFICATION_BODY_INSTALLATION;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_ISSUANCE;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.SYSTEM_MESSAGE_NOTIFICATION;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemAssignedToMeVerifierRepository.class})
class ItemAssignedToMeVerifierRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemAssignedToMeVerifierRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItemsAssignedTo() {
        Long account1 = 1L;
        Long account2 = 2L;
        Long account3 = 3L;
        Long vb1 = 1L;
        Long vb2 = 2L;

        String user = "user";
        String anotherUSer = "anotherUser";

        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(vb1, Set.of(
                INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                INSTALLATION_ACCOUNT_OPENING_ARCHIVE
                )
            );

        Request request2 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb1, LocalDateTime.now());
        createRequestTask(anotherUSer, request2, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", request2.getCreationDate());

        Request request3 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb1, LocalDateTime.now());
        RequestTask requestTask3 =
            createRequestTask(user, request3, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t3", request3.getCreationDate());

        Request request4 = createRequest(account1, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, vb1, LocalDateTime.now());
        RequestTask requestTask4 =
            createRequestTask(user, request4, NEW_VERIFICATION_BODY_INSTALLATION, "t4", request4.getCreationDate());

        Request request5 = createRequest(account1, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, vb1, LocalDateTime.now());
        createRequestTask(anotherUSer, request5, NEW_VERIFICATION_BODY_INSTALLATION, "t5", request5.getCreationDate());

        Request request6 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb1, LocalDateTime.now());
        RequestTask requestTask6 =
            createRequestTask(user, request6, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t6", request6.getCreationDate());

        Request request7 = createRequest(account3, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb2, LocalDateTime.now());
        createRequestTask(user, request7, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t7", request7.getCreationDate());

        Request request8 = createRequest(account3, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb2, LocalDateTime.now());
        createRequestTask(user, request8, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t8", request8.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedTo(user, scopedRequestTaskTypes, 0L, 10L);

        assertEquals(3L, itemPage.getTotalItems());
        assertEquals(3, itemPage.getItems().size());

        Optional<Item> optionalItem1 = itemPage.getItems().stream()
            .filter(item -> item.getTaskId().equals(requestTask3.getId()))
            .findFirst();

        Optional<Item> optionalItem2 = itemPage.getItems().stream()
            .filter(item -> item.getTaskId().equals(requestTask4.getId()))
            .findFirst();

        Optional<Item> optionalItem3 = itemPage.getItems().stream()
            .filter(item -> item.getTaskId().equals(requestTask6.getId()))
            .findFirst();

        assertThat(optionalItem1).isNotEmpty();
        assertThat(optionalItem2).isNotEmpty();
        assertThat(optionalItem3).isNotEmpty();

        assertThat(itemPage.getItems()).containsExactlyInAnyOrder(optionalItem1.get(), optionalItem2.get(), optionalItem3.get());
    }

    @Test
    void findItemsAssignedTo_task_assignee_not_in_vb() {
        Long account = 1L;
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, 2L, LocalDateTime.now());
        createRequestTask("vb2", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedTo("vb1", scopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedTo_completed_task() {
        Long account = 1L;
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now());
        createRequestTask("vb1", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
            "t1", request.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedTo("reg1", scopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedTo_two_items() {
        Long account1 = 1L;
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now());
        RequestTask requestTask1 = createRequestTask("vb1", request1, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                "t1", request1.getCreationDate());

        Long account2 = 2L;
        Request request2 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now().plusDays(1L));
        RequestTask requestTask2 = createRequestTask("vb1", request2, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                "t2", request2.getCreationDate());

        createOpenedItem(requestTask1.getId(), "vb1");

        ItemPage itemPage = itemRepository.findItemsAssignedTo("vb1", scopedRequestTaskTypes, 0L, 10L);

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        Item itemRequest1 = itemPage.getItems().
                stream().filter(i -> i.getRequestId().equals(request1.getId()))
                .findFirst().orElseThrow();
        assertEquals(itemRequest1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(itemRequest1.getRequestId(), request1.getId());
        assertEquals(itemRequest1.getRequestType(), request1.getType());
        assertEquals(itemRequest1.getTaskId(), requestTask1.getId());
        assertEquals(itemRequest1.getTaskType(), requestTask1.getType());
        assertEquals(itemRequest1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(itemRequest1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(itemRequest1.getAccountId(), account1);
        assertFalse(itemRequest1.isNew());

        Item itemRequest2 =  itemPage.getItems().
                stream().filter(i -> i.getRequestId().equals(request2.getId()))
                .findFirst().orElseThrow();
        assertEquals(itemRequest2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
                requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(itemRequest2.getRequestId(), request2.getId());
        assertEquals(itemRequest2.getRequestType(), request2.getType());
        assertEquals(itemRequest2.getTaskId(), requestTask2.getId());
        assertEquals(itemRequest2.getTaskType(), requestTask2.getType());
        assertEquals(itemRequest2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(itemRequest2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(itemRequest2.getAccountId(), account2);
        assertTrue(itemRequest2.isNew());
    }

    @Test
    void findItemsAssignedTo_one_task_and_one_system_notification() {
        Long account1 = 1L;
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now());
        RequestTask requestTask1 = createRequestTask("vb1", request1, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
            "t1", request1.getCreationDate());

        Long account2 = 2L;
        Request request2 = createRequest(account2, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, verificationBody, LocalDateTime.now().plusDays(1L));
        RequestTask requestTask2 = createRequestTask("vb1", request2, NEW_VERIFICATION_BODY_INSTALLATION,
            "t2", request2.getCreationDate());

        createOpenedItem(requestTask1.getId(), "vb1");

        ItemPage itemPage = itemRepository.findItemsAssignedTo("vb1", scopedRequestTaskTypes, 0L, 10L);

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        Item itemRequest1 = itemPage.getItems().
            stream().filter(i -> i.getRequestId().equals(request1.getId()))
            .findFirst().orElseThrow();
        assertEquals(itemRequest1.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
            requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(itemRequest1.getRequestId(), request1.getId());
        assertEquals(itemRequest1.getRequestType(), request1.getType());
        assertEquals(itemRequest1.getTaskId(), requestTask1.getId());
        assertEquals(itemRequest1.getTaskType(), requestTask1.getType());
        assertEquals(itemRequest1.getTaskAssigneeId(), requestTask1.getAssignee());
        assertEquals(itemRequest1.getTaskDueDate(), requestTask1.getDueDate());
        assertEquals(itemRequest1.getAccountId(), account1);
        assertFalse(itemRequest1.isNew());

        Item itemRequest2 =  itemPage.getItems().
            stream().filter(i -> i.getRequestId().equals(request2.getId()))
            .findFirst().orElseThrow();
        assertEquals(itemRequest2.getCreationDate().truncatedTo(ChronoUnit.MILLIS),
            requestTask2.getStartDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(itemRequest2.getRequestId(), request2.getId());
        assertEquals(itemRequest2.getRequestType(), request2.getType());
        assertEquals(itemRequest2.getTaskId(), requestTask2.getId());
        assertEquals(itemRequest2.getTaskType(), requestTask2.getType());
        assertEquals(itemRequest2.getTaskAssigneeId(), requestTask2.getAssignee());
        assertEquals(itemRequest2.getTaskDueDate(), requestTask2.getDueDate());
        assertEquals(itemRequest2.getAccountId(), account2);
        assertTrue(itemRequest2.isNew());
    }

    @Test
    void findItemsAssignedTo_only_one_item_unauthorized_for_user() {
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        Long account = 1L;
        Request request = createRequest(account, PERMIT_ISSUANCE, IN_PROGRESS, verificationBody, LocalDateTime.now());
        createRequestTask("vb1", request, PERMIT_ISSUANCE_APPLICATION_REVIEW, "t1", request.getCreationDate());

        ItemPage itemPage =
            itemRepository.findItemsAssignedTo("vb1", scopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }


    @Test
    void findItemsAssignedToByAccount_task_assignee_false() {
        Long account = 1L;
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request = createRequest(account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now());
        createRequestTask("oper1", request, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t1", request.getCreationDate());

        ItemPage itemPage =
            itemRepository.findItemsAssignedToByAccount("vb1", account, scopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
        assertTrue(itemPage.getItems().isEmpty());
    }

    @Test
    void findItemsAssignedToByAccount_task_assignee_true() {
        Long account = 1L;
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        Request request = createRequest( account, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now());
        RequestTask requestTask = createRequestTask("vb1", request, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                "t1", request.getCreationDate());

        createOpenedItem(requestTask.getId(), "vb1");

        ItemPage itemPage =
            itemRepository.findItemsAssignedToByAccount("vb1", account, scopedRequestTaskTypes, 0L, 10L);

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
    void findItemsAssignedToByAccount_two_requests_two_tasks_different_accounts() {
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        Long account1 = 1L;
        Request request1 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now());
        RequestTask requestTask1 =
                createRequestTask("vb1", request1, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t1", request1.getCreationDate());
        Long account2 = 2L;
        Request request2 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, verificationBody, LocalDateTime.now().plusDays(1));
        createRequestTask("vb1", request2, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", request2.getCreationDate());

        ItemPage itemPage =
            itemRepository.findItemsAssignedToByAccount("vb1", account1, scopedRequestTaskTypes, 0L, 10L);

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1, itemPage.getItems().size());
        Item item = itemPage.getItems().get(0);
        assertEquals(requestTask1.getStartDate().truncatedTo(ChronoUnit.MILLIS),
                item.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request1.getId(), item.getRequestId());
        assertEquals(request1.getType(), item.getRequestType());
        assertEquals(requestTask1.getId(), item.getTaskId());
        assertEquals(requestTask1.getType(), item.getTaskType());
        assertEquals(requestTask1.getAssignee(), item.getTaskAssigneeId());
        assertEquals(requestTask1.getDueDate(), item.getTaskDueDate());
        assertEquals(account1, item.getAccountId());
        assertTrue(item.isNew());
    }

    @Test
    void findItemsAssignedToByAccount_only_system_notification() {
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        Long account = 1L;
        Request request = createRequest(account, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, verificationBody, LocalDateTime.now());
        RequestTask requestTask =
            createRequestTask("vb1", request, NEW_VERIFICATION_BODY_INSTALLATION, "t1", request.getCreationDate());

        ItemPage itemPage =
            itemRepository.findItemsAssignedToByAccount("vb1", account, scopedRequestTaskTypes, 0L, 10L);

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
    void findItemsAssignedToByAccount_only_one_item_unauthorized_for_user() {
        Long verificationBody = 1L;
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(verificationBody, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));
        Long account = 1L;
        Request request = createRequest(account, PERMIT_ISSUANCE, IN_PROGRESS, verificationBody, LocalDateTime.now());
        createRequestTask("vb1", request, PERMIT_ISSUANCE_APPLICATION_REVIEW, "t1", request.getCreationDate());

        ItemPage itemPage =
            itemRepository.findItemsAssignedToByAccount("vb1", account, scopedRequestTaskTypes, 0L, 10L);

        assertEquals(0L, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsAssignedToByAccount() {
        Long account1 = 1L;
        Long account2 = 2L;
        Long vb1 = 1L;

        String user = "user";
        String anotherUSer = "anotherUser";

        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
            Map.of(vb1, Set.of(
                INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW,
                INSTALLATION_ACCOUNT_OPENING_ARCHIVE
                )
            );

        Request request2 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb1, LocalDateTime.now());
        createRequestTask(anotherUSer, request2, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t2", request2.getCreationDate());

        Request request3 = createRequest(account1, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb1, LocalDateTime.now());
        RequestTask requestTask3 =
            createRequestTask(user, request3, INSTALLATION_ACCOUNT_OPENING_ARCHIVE, "t3", request3.getCreationDate());

        Request request4 = createRequest(account1, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, vb1, LocalDateTime.now());
        RequestTask requestTask4 =
            createRequestTask(user, request4, NEW_VERIFICATION_BODY_INSTALLATION, "t4", request4.getCreationDate());

        Request request5 = createRequest(account1, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, vb1, LocalDateTime.now());
        createRequestTask(anotherUSer, request5, NEW_VERIFICATION_BODY_INSTALLATION, "t5", request5.getCreationDate());

        Request request6 = createRequest(account2, INSTALLATION_ACCOUNT_OPENING, IN_PROGRESS, vb1, LocalDateTime.now());
        createRequestTask(user, request6, INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW, "t6", request6.getCreationDate());

        Request request7 = createRequest(account2, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, vb1, LocalDateTime.now());
        createRequestTask(user, request7, NEW_VERIFICATION_BODY_INSTALLATION, "t7", request7.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsAssignedToByAccount(user, account1, scopedRequestTaskTypes, 0L, 10L);

        assertEquals(2L, itemPage.getTotalItems());
        assertEquals(2, itemPage.getItems().size());

        Optional<Item> optionalItem1 = itemPage.getItems().stream()
            .filter(item -> item.getTaskId().equals(requestTask3.getId()))
            .findFirst();

        Optional<Item> optionalItem2 = itemPage.getItems().stream()
            .filter(item -> item.getTaskId().equals(requestTask4.getId()))
            .findFirst();

        assertThat(optionalItem1).isNotEmpty();
        assertThat(optionalItem2).isNotEmpty();

        assertThat(itemPage.getItems()).containsExactlyInAnyOrder(optionalItem1.get(), optionalItem2.get());
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, Long vbId, LocalDateTime creationDate) {
        Request request = Request.builder()
                .id(RandomStringUtils.random(5))
                .competentAuthority(ENGLAND)
                .type(type)
                .status(status)
                .accountId(accountId)
                .verificationBodyId(vbId)
                .creationDate(creationDate)
                .build();

        entityManager.persist(request);

        return request;
    }

    private RequestTask createRequestTask(String assignee, Request request, RequestTaskType taskType,
            String processTaskId, LocalDateTime startDate) {
        RequestTask requestTask = RequestTask.builder()
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
        RequestTaskVisit requestTaskVisit = RequestTaskVisit.builder()
                .taskId(taskId)
                .userId(userId).build();

        entityManager.persist(requestTaskVisit);
    }
}
