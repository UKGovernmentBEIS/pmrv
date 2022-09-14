package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, PermitVariationRequestIdGenerator.class})
class PermitVariationRequestIdGeneratorIT extends AbstractContainerBaseTest {

    @Autowired
    private PermitVariationRequestIdGenerator generator;
    
    @Autowired
    private RequestSequenceRepository repository;

    @Test
    void shouldGenerateRequestId() {
    	Long accountId = 1L;
        String requestId = generator.generate(RequestParams.builder()
            .accountId(accountId)
            .type(RequestType.PERMIT_VARIATION)
            .build());

        assertThat(requestId).isEqualTo("AEMV1-1");
        
        RequestSequence seq =
            repository.findByAccountIdAndType(accountId, RequestType.PERMIT_VARIATION).get();
        assertThat(seq.getVersion()).isZero();
    }

}
