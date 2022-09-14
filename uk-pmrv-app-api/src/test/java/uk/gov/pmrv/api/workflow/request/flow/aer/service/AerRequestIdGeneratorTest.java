package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AerRequestIdGeneratorTest {

    @InjectMocks
    private AerRequestIdGenerator generator;

    @Test
    void generate() {
        RequestParams params = RequestParams.builder()
                .accountId(12L)
                .requestMetadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .year(Year.of(2022))
                        .build())
                .build();

        String requestId = generator.generate(params);

        assertEquals("AEM12-2022", requestId);
    }

    @Test
    void getType() {
        RequestType type = generator.getType();

        assertEquals(RequestType.AER, type);
    }

    @Test
    void getPrefix() {
        String prefix = generator.getPrefix();

        assertEquals("AEM", prefix);
    }
}
