package uk.gov.pmrv.api.workflow.request.core.repository;

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
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RequestRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestRepository requestRepository;
    
    @Autowired
	private EntityManager entityManager;
    
    @Test
    void updateVerificationBodyIdByAccountId() {
        Long accountId = 1L;
        Long anotherAccountId = 2L;
        Long vbOld = 1L;
        Long vbNew = 2L;
        
        Request request1 = createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, vbOld);
        Request request2 = createRequest(anotherAccountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, vbOld);
        
        assertThat(request1.getVerificationBodyId()).isEqualTo(vbOld);
        
        //invoke
        requestRepository.updateVerificationBodyIdByAccountId(vbNew, accountId);
        
        flushAndClear();
        
        request1 = requestRepository.findById(request1.getId()).get();
        assertThat(request1.getVerificationBodyId()).isEqualTo(vbNew);
        
        request2 = requestRepository.findById(request2.getId()).get();
        assertThat(request2.getVerificationBodyId()).isEqualTo(vbOld);
    }
    
    @Test
    void findByAccountIdAndStatusAndTypeNotNotification() {
        Long accountId = 1L;
        createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.COMPLETED);
        Request request = createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS);
        createRequest(accountId, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS);

        flushAndClear();
        
        List<Request> result = requestRepository.findByAccountIdAndStatusAndTypeNotNotification(accountId, RequestStatus.IN_PROGRESS);
        
        assertThat(result).containsExactlyInAnyOrder(request);
    }
    
    private Request createRequest(Long accountId, RequestType type, RequestStatus status, Long verificationBodyId) {
        Request request = 
                Request.builder()
                    .id(RandomStringUtils.random(5))
                    .accountId(accountId)
                    .type(type)
                    .status(status)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .verificationBodyId(verificationBodyId)
                    .build();
        entityManager.persist(request);
        return request;
    }
    
    private Request createRequest(Long accountId, RequestType type, RequestStatus status) {
    	return createRequest(accountId, type, status, null);
    }
    
    private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}

}
