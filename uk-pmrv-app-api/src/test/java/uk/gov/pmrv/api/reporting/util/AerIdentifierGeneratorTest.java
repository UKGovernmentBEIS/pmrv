package uk.gov.pmrv.api.reporting.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AerIdentifierGeneratorTest {

    @Test
    void generate() {
        Long accountId = 2353L;
        int reportingYear = 2022;

        String aerId = AerIdentifierGenerator.generate(accountId, reportingYear);

        assertThat(aerId).isEqualTo("AEM2353-2022");
    }
}