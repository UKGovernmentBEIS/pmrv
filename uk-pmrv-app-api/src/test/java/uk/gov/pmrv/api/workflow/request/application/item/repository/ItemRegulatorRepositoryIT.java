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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.WALES;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.COMPLETED;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.PERMIT_ISSUANCE_WAIT_FOR_AMENDS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.PERMIT_ISSUANCE;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemRegulatorRepository.class})
class ItemRegulatorRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemRegulatorRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findItemsByRequestId() {
        Long accountId = 1L;
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(PERMIT_ISSUANCE_WAIT_FOR_AMENDS));

        Request request1 = createRequest(accountId, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("oper1", request1, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t1", request1.getCreationDate());
        Request request2 = createRequest(accountId, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request2, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t2", request2.getCreationDate());
        RequestTask requestTask = createRequestTask("reg1", request2, PERMIT_ISSUANCE_WAIT_FOR_AMENDS, "t3", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsByRequestId(scopedRequestTaskTypes, request2.getId());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1L, itemPage.getItems().size());
        Item item = itemPage.getItems().get(0);
        assertEquals(requestTask.getStartDate().truncatedTo(ChronoUnit.MILLIS),
                item.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request2.getId(), item.getRequestId());
        assertEquals(request2.getType(), item.getRequestType());
        assertEquals(requestTask.getId(), item.getTaskId());
        assertEquals(requestTask.getType(), item.getTaskType());
        assertEquals(requestTask.getAssignee(), item.getTaskAssigneeId());
        assertEquals(requestTask.getDueDate(), item.getTaskDueDate());
        assertEquals(accountId, item.getAccountId());
    }

    @Test
    void findItemsByRequestId_as_CLOSED() {
        Long accountId = 1L;
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(PERMIT_ISSUANCE_WAIT_FOR_AMENDS));

        Request request1 = createRequest(accountId, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("oper1", request1, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t1", request1.getCreationDate());
        Request request2 = createRequest(accountId, PERMIT_ISSUANCE, COMPLETED, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request2, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t2", request2.getCreationDate());
        RequestTask requestTask = createRequestTask("reg1", request2, PERMIT_ISSUANCE_WAIT_FOR_AMENDS, "t3", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsByRequestId(scopedRequestTaskTypes, request2.getId());

        assertEquals(1L, itemPage.getTotalItems());
        assertEquals(1L, itemPage.getItems().size());
        Item item = itemPage.getItems().get(0);
        assertEquals(requestTask.getStartDate().truncatedTo(ChronoUnit.MILLIS),
                item.getCreationDate().truncatedTo(ChronoUnit.MILLIS));
        assertEquals(request2.getId(), item.getRequestId());
        assertEquals(request2.getType(), item.getRequestType());
        assertEquals(requestTask.getId(), item.getTaskId());
        assertEquals(requestTask.getType(), item.getTaskType());
        assertEquals(requestTask.getAssignee(), item.getTaskAssigneeId());
        assertEquals(requestTask.getDueDate(), item.getTaskDueDate());
        assertEquals(accountId, item.getAccountId());
    }

    @Test
    void findItemsByRequestId_no_privilege_on_ca() {
        Long accountId = 1L;
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(PERMIT_ISSUANCE_WAIT_FOR_AMENDS));

        Request request1 = createRequest(accountId, PERMIT_ISSUANCE, IN_PROGRESS, WALES, LocalDateTime.now());
        createRequestTask("oper1", request1, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t1", request1.getCreationDate());
        Request request2 = createRequest(accountId, PERMIT_ISSUANCE, COMPLETED, WALES, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request2, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t2", request2.getCreationDate());
        RequestTask requestTask = createRequestTask("reg1", request2, PERMIT_ISSUANCE_WAIT_FOR_AMENDS, "t3", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsByRequestId(scopedRequestTaskTypes, request2.getId());

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsByRequestId_no_task() {
        Long accountId = 1L;
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(PERMIT_ISSUANCE_WAIT_FOR_AMENDS));

        Request request = createRequest(accountId, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());

        ItemPage itemPage = itemRepository.findItemsByRequestId(scopedRequestTaskTypes, request.getId());

        assertEquals(0, itemPage.getTotalItems());
        assertEquals(0, itemPage.getItems().size());
    }

    @Test
    void findItemsByRequestId_no_view_permission() {
        Long accountId = 1L;
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes =
                Map.of(ENGLAND, Set.of(PERMIT_ISSUANCE_APPLICATION_REVIEW));

        Request request1 = createRequest(accountId, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now());
        createRequestTask("oper1", request1, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t1", request1.getCreationDate());
        Request request2 = createRequest(accountId, PERMIT_ISSUANCE, IN_PROGRESS, ENGLAND, LocalDateTime.now().plusDays(1));
        createRequestTask("oper1", request2, PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT, "t2", request2.getCreationDate());
        createRequestTask("reg1", request2, PERMIT_ISSUANCE_WAIT_FOR_AMENDS, "t3", request2.getCreationDate());

        ItemPage itemPage = itemRepository.findItemsByRequestId(scopedRequestTaskTypes, request2.getId());

        assertEquals(0, itemPage.getTotalItems());
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
}
