package uk.gov.pmrv.api.workflow.request.application.item.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.annotation.DirtiesContext;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.application.item.domain.RequestTaskVisit;
import uk.gov.pmrv.api.workflow.request.application.item.domain.RequestTaskVisitPK;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RequestTaskVisitRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestTaskVisitRepository requestTaskVisitRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deleteByTaskId() {

        Long taskId = 1L;
        String userId = "userId";
        RequestTaskVisit requestTaskVisit = createOpenedItemRequest(taskId, userId);

        RequestTaskVisit result = entityManager.find(RequestTaskVisit.class, new RequestTaskVisitPK(taskId, userId));
        assertThat(result).isEqualTo(requestTaskVisit);

		requestTaskVisitRepository.deleteByTaskId(taskId);
		
        result = entityManager.find(RequestTaskVisit.class, new RequestTaskVisitPK(taskId, userId));
        assertThat(result).isNull();
    }

    private RequestTaskVisit createOpenedItemRequest(Long taskId, String userId) {
        RequestTaskVisit requestTaskVisit = RequestTaskVisit.builder()
            .taskId(taskId)
            .userId(userId)
            .build();

        entityManager.persist(requestTaskVisit);

        return requestTaskVisit;
    }

}
