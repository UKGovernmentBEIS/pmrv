package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningRequestIdGeneratorTest {

    @InjectMocks
    private InstallationAccountOpeningRequestIdGenerator generator;

    @Test
    void generate_no_padding() {

        final RequestParams params = RequestParams.builder().accountId(12345L).build();

        final String requestId = generator.generate(params);

        assertThat(requestId).isEqualTo("NEW12345");
    }

    @Test
    void generate_with_padding() {

        final RequestParams params = RequestParams.builder().accountId(1L).build();

        final String requestId = generator.generate(params);

        assertThat(requestId).isEqualTo("NEW00001");
    }
}
