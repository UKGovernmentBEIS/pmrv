package uk.gov.pmrv.api.workflow.request.core.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.INSTALLATION_ACCOUNT_OPENING;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RequestActionRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestActionRepository requestActionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByRequestId() {
        Request request1 = Request.builder()
            .id("1")
            .type(INSTALLATION_ACCOUNT_OPENING)
            .status(IN_PROGRESS)
            .creationDate(LocalDateTime.now())
            .build();

        RequestAction requestAction1 = RequestAction.builder()
            .request(request1)
            .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
            .submitterId("userId")
            .submitter("username")
            .creationDate(LocalDateTime.now())
            .build();

        entityManager.persist(request1);
        entityManager.persist(requestAction1);

        Request request2 = Request.builder()
            .id("2")
            .type(INSTALLATION_ACCOUNT_OPENING)
            .status(IN_PROGRESS)
            .creationDate(LocalDateTime.now())
            .build();

        RequestAction requestAction2 = RequestAction.builder()
            .request(request2)
            .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
            .submitterId("userId")
            .submitter("username")
            .creationDate(LocalDateTime.now())
            .build();

        entityManager.persist(request2);
        entityManager.persist(requestAction2);

        List<RequestAction> actual = requestActionRepository.findAllByRequestId(request1.getId());

        assertThat(actual)
                .hasSize(1)
                .containsExactly(requestAction1);
    }
}
