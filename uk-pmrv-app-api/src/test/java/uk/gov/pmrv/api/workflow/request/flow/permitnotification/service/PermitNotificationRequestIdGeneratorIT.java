package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class, PermitNotificationRequestIdGenerator.class})
class PermitNotificationRequestIdGeneratorIT extends AbstractContainerBaseTest {

    public static final long TEST_ACCOUNT_ID = 1L;

    @Autowired
    private PermitNotificationRequestIdGenerator generator;

    @Autowired
    private RequestSequenceRepository repository;

    @Test
    void shouldGenerateRequestId() {
        final String requestId = generator.generate(RequestParams.builder()
                .accountId(TEST_ACCOUNT_ID)
                .type(RequestType.PERMIT_NOTIFICATION)
                .build());

        assertThat(requestId).isEqualTo("AEMN1-1");

        Optional<RequestSequence> seq = repository.findByAccountIdAndType(TEST_ACCOUNT_ID, RequestType.PERMIT_NOTIFICATION);

        assertThat(seq).isPresent();
        assertThat(seq.get().getVersion()).isZero();
    }

    @Test
    void shouldGenerateRequestIdWhenSequenceAlreadyExists() {
        final RequestParams params = RequestParams.builder()
                .accountId(TEST_ACCOUNT_ID)
                .type(RequestType.PERMIT_NOTIFICATION)
                .build();

        generator.generate(params);
        assertThat(generator.generate(params)).isEqualTo("AEMN1-2");

        Optional<RequestSequence> seq = repository.findByAccountIdAndType(TEST_ACCOUNT_ID, RequestType.PERMIT_NOTIFICATION);

        assertThat(seq).isPresent();
        assertThat(seq.get().getVersion()).isEqualTo(1);
    }

    @Test
    void getType() {
        assertThat(generator.getType()).isEqualTo(RequestType.PERMIT_NOTIFICATION);
    }
}
