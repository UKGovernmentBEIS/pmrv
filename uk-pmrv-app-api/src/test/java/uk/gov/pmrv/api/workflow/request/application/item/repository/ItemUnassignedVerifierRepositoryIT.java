package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, ItemUnassignedVerifierRepository.class})
class ItemUnassignedVerifierRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private ItemUnassignedVerifierRepository itemRepository;

    @Sql(statements = {
            //request insert statements
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES (1, 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:28:22.762427', 'process_instance_id1', 'ENGLAND', 1, 1)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES (2, 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:20:22.762427', 'process_instance_id2', 'WALES', 2, 1)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES (3, 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-21 07:20:22.762427', 'process_instance_id3', 'SCOTLAND', 3, 2)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES (4, 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-21 06:20:22.762427', 'process_instance_id4', 'ENGLAND', 4, 2)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES (5, 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-19 07:25:22.762427', 'process_instance_id5', 'ENGLAND', 5, 2)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES (6, 'INSTALLATION_ACCOUNT_OPENING', 'COMPLETED', '2020-10-19 10:25:22.762427', 'process_instance_id6', 'ENGLAND', 6, 2)",
            //request task insert statements
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee, due_date, start_date, end_date, version) " +
                    "VALUES (1, 1, 'process_task_id1', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null, null, '2020-10-22 07:28:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee, due_date, start_date, end_date, version) " +
                    "VALUES (2, 2, 'process_task_id2', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null, null, '2020-10-22 07:21:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee, due_date, start_date, end_date, version) " +
                    "VALUES (3, 3, 'process_task_id3', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null, null, '2020-10-21 07:21:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee, due_date, start_date, end_date, version) " +
                    "VALUES (4, 4, 'process_task_id4', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', 'assignee', null, '2020-10-21 06:21:23.248905', null, 0)",
            //opened item insert statements
            "INSERT INTO request_task_visit(task_id, user_id) VALUES(1, 'user')"
    })
    @Test
    void findUnassignedItems() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        ItemPage itemPage = itemRepository.findUnassignedItems("user", scopedRequestTaskTypes, 0L, 10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).hasSize(2);
        assertThat(itemPage.getTotalItems()).isEqualTo(2L);

        Item item1 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals("1"))
                .findFirst().orElse(new Item());
        assertThat(item1.getTaskId()).isEqualTo(1L);
        assertThat(item1.getTaskAssigneeId()).isNull();
        assertFalse(item1.isNew());

        Item item2 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals("2"))
                .findFirst().orElse(new Item());
        assertThat(item2.getTaskId()).isEqualTo(2L);
        assertThat(item2.getTaskAssigneeId()).isNull();
        assertTrue(item2.isNew());

        //assert order by start_date desc in list
        assertTrue(itemPage.getItems().get(0).getCreationDate().isAfter(itemPage.getItems().get(1).getCreationDate()));
    }

    @Sql(statements = {
            //request insert statements
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('1', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:28:22.762427', 'process_instance_id1', 'ENGLAND', 1, 1)",
            //request task insert statements
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee, due_date, start_date, end_date, version) " +
                    "VALUES (1, '1', 'process_task_id1', 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE', null, null, '2020-10-22 07:28:23.248905', null, 0)",
    })
    @Test
    void findUnassignedItemsReturnsNoItemsWhenPermissionsDoNotMatch() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        ItemPage itemPage = itemRepository.findUnassignedItems("user", scopedRequestTaskTypes, 0L, 10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).isEmpty();
        assertThat(itemPage.getTotalItems()).isZero();
    }

    @Test
    void findUnassignedItemsReturnsNoItemsWhenUserNoInputPermissions() {
        ItemPage itemPage = itemRepository.findUnassignedItems("user", Map.of(), 0L, 10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).isEmpty();
        assertThat(itemPage.getTotalItems()).isZero();
    }

    @Sql(statements = {
            //request insert statements
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('1', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:28:22.762427', 'process_instance_id1', 'ENGLAND', 1, 1)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('2', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:20:22.762427', 'process_instance_id2', 'WALES', 1, 1)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id) " +
                    "VALUES ('3', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-21 07:20:22.762427', 'process_instance_id3', 'SCOTLAND', 1)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id) " +
                    "VALUES ('4', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-21 06:20:22.762427', 'process_instance_id4', 'ENGLAND', 1)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id) " +
                    "VALUES ('5', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-19 07:25:22.762427', 'process_instance_id5', 'ENGLAND', 1)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id) " +
                    "VALUES ('6', 'INSTALLATION_ACCOUNT_OPENING', 'COMPLETED', '2020-10-19 10:25:22.762427', 'process_instance_id6', 'ENGLAND', 1)",
            //request task insert statements
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (1, '1', 'process_task_id1', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null,  null, '2020-10-22 07:28:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (2, '2', 'process_task_id2', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null,  null, '2020-10-22 07:21:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (3, '3', 'process_task_id3', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null,  null, '2020-10-21 07:21:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (4, '4', 'process_task_id4', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', 'assignee',  null, '2020-10-21 06:21:23.248905', null, 0)",
    })
    @Test
    void findUnassignedItemsByAccount() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        ItemPage itemPage = itemRepository.findUnassignedItemsByAccount("user", scopedRequestTaskTypes, 1L, 0L, 10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).hasSize(2);
        assertThat(itemPage.getTotalItems()).isEqualTo(2L);

        Item item1 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals("1"))
                .findFirst().orElse(new Item());
        assertThat(item1.getTaskId()).isEqualTo(1L);
        assertThat(item1.getTaskAssigneeId()).isNull();
        assertTrue(item1.isNew());

        Item item2 = itemPage.getItems().stream()
                .filter(item -> item.getRequestId().equals("2"))
                .findFirst().orElse(new Item());
        assertThat(item2.getTaskId()).isEqualTo(2L);
        assertThat(item2.getTaskAssigneeId()).isNull();
        assertTrue(item2.isNew());

        //assert order by start_date desc in list
        assertTrue(itemPage.getItems().get(0).getCreationDate().isAfter(itemPage.getItems().get(1).getCreationDate()));
    }

    @Sql(statements = {
            //request insert statements
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('1', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:28:22.762427', 'process_instance_id1', 'ENGLAND', 1, 1)",
            //request task insert statements
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (1, '1', 'process_task_id1', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', 'userId',  null, '2020-10-22 07:28:23.248905', null, 0)",
    })
    @Test
    void findUnassignedItemsByAccountReturnsNoItemsWhenAssigneeNotNull() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        ItemPage itemPage = itemRepository.findUnassignedItemsByAccount("user", scopedRequestTaskTypes, 2L, 0L, 10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).isEmpty();
        assertThat(itemPage.getTotalItems()).isZero();
    }

    @Sql(statements = {
            //request insert statements
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('1', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:28:22.762427', 'process_instance_id1', 'ENGLAND', 1, 1)",
            //request task insert statements
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (1, '1', 'process_task_id1', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null,  null, '2020-10-22 07:28:23.248905', null, 0)",
    })
    @Test
    void findUnassignedItemsByAccountReturnsNoItemsWhenNoAccountExists() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        ItemPage itemPage = itemRepository.findUnassignedItemsByAccount("user", scopedRequestTaskTypes, 2L, 0L, 10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).isEmpty();
        assertThat(itemPage.getTotalItems()).isZero();
    }

    @Sql(statements = {
            //request insert statements
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('1', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:28:22.762427', 'process_instance_id1', 'ENGLAND', 1, 2)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('2', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-22 07:20:22.762427', 'process_instance_id2', 'WALES', 1, 2)",
            "INSERT INTO request (id, type, status, creation_date, process_instance_id, competent_authority, account_id, verification_body_id) " +
                    "VALUES ('3', 'INSTALLATION_ACCOUNT_OPENING', 'IN_PROGRESS', '2020-10-21 07:20:22.762427', 'process_instance_id3', 'SCOTLAND', 1, 3)",
            //request task insert statements
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (1, '1', 'process_task_id1', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null,  null, '2020-10-22 07:28:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (2, '2', 'process_task_id2', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null,  null, '2020-10-22 07:21:23.248905', null, 0)",
            "INSERT INTO request_task (id, request_id, process_task_id, type, assignee,  due_date, start_date, end_date, version) " +
                    "VALUES (3, '3', 'process_task_id3', 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW', null,  null, '2020-10-21 07:21:23.248905', null, 0)"
    })
    @Test
    void findUnassignedItemsByAccountReturnsNoItemsWhenNotInVB() {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW));

        ItemPage itemPage = itemRepository.findUnassignedItemsByAccount("user", scopedRequestTaskTypes, 1L, 0L, 10L);

        assertThat(itemPage).isNotNull();
        assertThat(itemPage.getItems()).isEmpty();
        assertThat(itemPage.getTotalItems()).isZero();
    }
}
