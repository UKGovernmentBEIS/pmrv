package uk.gov.pmrv.api.workflow.request.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestStatusCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, RequestDetailsRepository.class})
class RequestDetailsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private RequestDetailsRepository repo;

    @Autowired
    private EntityManager entityManager;
    
    @Test
    void findRequestDetailsBySearchCriteria() {
        Long accountId = 1L;
        Request request1 = createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        Request request2 = createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        createRequest(accountId, RequestType.SYSTEM_MESSAGE_NOTIFICATION, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .page(0L)
                .pageSize(30L)
                .category(RequestCategory.PERMIT)
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request1.getId(), request1.getType(), request1.getStatus(), request1.getCreationDate(), null);
        RequestDetailsDTO expectedWorkflowResult2 = new RequestDetailsDTO(request2.getId(), request2.getType(), request2.getStatus(), request2.getCreationDate(), null);
        
        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(2L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult2, expectedWorkflowResult1
                ));
    }
    
    @Test
    void findRequestDetailsBySearchCriteria_filter_by_status_open() {
        Long accountId = 1L;
        Request request1 = createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthority.ENGLAND);
        createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .status(RequestStatusCategory.OPEN)
                .page(0L)
                .pageSize(30L)
                .category(RequestCategory.PERMIT)
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request1.getId(), request1.getType(), request1.getStatus(), request1.getCreationDate(), null);
        
        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(1L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult1
                ));
    }
    
    @Test
    void findRequestDetailsBySearchCriteria_filter_by_status_closed() {
        Long accountId = 1L;
        AerRequestMetadata metadata = AerRequestMetadata.builder().type(RequestMetadataType.AER)
                .emissions(BigDecimal.valueOf(10000)).build();
        createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        Request request2 = createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthority.ENGLAND);
        Request request3 = createRequest(accountId, RequestType.AER, RequestStatus.COMPLETED, CompetentAuthority.ENGLAND, metadata);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .status(RequestStatusCategory.CLOSED)
                .page(0L)
                .pageSize(30L)
                .category(RequestCategory.PERMIT)
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request2.getId(), request2.getType(), request2.getStatus(), request2.getCreationDate(), null);

        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(1L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult1
        ));
    }

    @Test
    void findRequestDetailsBySearchCriteria_filter_by_status_closed_and_reporting() {
        Long accountId = 1L;
        AerRequestMetadata metadata = AerRequestMetadata.builder().type(RequestMetadataType.AER)
                .emissions(BigDecimal.valueOf(10000)).build();
        createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        Request request2 = createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthority.ENGLAND);
        Request request3 = createRequest(accountId, RequestType.AER, RequestStatus.COMPLETED, CompetentAuthority.ENGLAND, metadata);

        flushAndClear();

        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .status(RequestStatusCategory.CLOSED)
                .page(0L)
                .pageSize(30L)
                .category(RequestCategory.REPORTING)
                .build();

        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);

        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request3.getId(), request3.getType(), request3.getStatus(), request3.getCreationDate(), metadata);

        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(1L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult1
        ));
    }
    
    @Test
    void findRequestDetailsBySearchCriteria_filter_by_type() {
        Long accountId = 1L;
        Request request1 = createRequest(accountId, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        createRequest(accountId, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthority.ENGLAND);
        createRequest(2L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);

        flushAndClear();
        
        RequestSearchCriteria criteria = RequestSearchCriteria.builder()
                .accountId(accountId)
                .requestTypes(Set.of(RequestType.INSTALLATION_ACCOUNT_OPENING))
                .page(0L)
                .pageSize(30L)
                .category(RequestCategory.PERMIT)
                .build();
        
        RequestDetailsSearchResults results = repo.findRequestDetailsBySearchCriteria(criteria);
        
        RequestDetailsDTO expectedWorkflowResult1 = new RequestDetailsDTO(request1.getId(), request1.getType(), request1.getStatus(), request1.getCreationDate(), null);
        
        assertThat(results).isNotNull();
        assertThat(results.getTotal()).isEqualTo(1L);
        assertThat(results.getRequestDetails()).isEqualTo(List.of(
                expectedWorkflowResult1
                ));
    }

    @Test
    void findRequestDetailsById() {
        Request request = createRequest(1L, RequestType.INSTALLATION_ACCOUNT_OPENING, RequestStatus.IN_PROGRESS, CompetentAuthority.ENGLAND);
        createRequest(1L, RequestType.PERMIT_ISSUANCE, RequestStatus.COMPLETED, CompetentAuthority.ENGLAND);
        flushAndClear();

        RequestDetailsDTO actual = repo.findRequestDetailsById(request.getId());

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(request.getId());
        assertThat(actual.getRequestType()).isEqualTo(request.getType());
        assertThat(actual.getRequestStatus()).isEqualTo(request.getStatus());
        assertThat(actual.getCreationDate()).isEqualTo(request.getCreationDate().toLocalDate());
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthority ca) {
        return createRequest(accountId, type, status, ca, null);
    }
    
    private Request createRequest(Long accountId, RequestType type, RequestStatus status, CompetentAuthority ca, RequestMetadata metaData) {
        Request request =
            Request.builder()
                    .id(RandomStringUtils.random(5))
                    .competentAuthority(ca)
                    .type(type)
                    .status(status)
                    .accountId(accountId)
                    .metadata(metaData)
                    .build();

        entityManager.persist(request);

        return request;
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
