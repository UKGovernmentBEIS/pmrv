package uk.gov.pmrv.api.workflow.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.BankAccountDetails;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class BankAccountDetailsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private BankAccountDetailsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthority() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        BankAccountDetails expectedBankAccountDetails = BankAccountDetails.builder()
            .competentAuthority(competentAuthority)
            .accountName("accountName")
            .sortCode("sortCode")
            .accountNumber("accountNumber")
            .iban("iban")
            .swiftCode("swiftCode")
            .build();

        entityManager.persist(expectedBankAccountDetails);

        flushAndClear();

        Optional<BankAccountDetails> result = repository.findByCompetentAuthority(competentAuthority);

        assertThat(result).isNotEmpty();
        assertEquals(expectedBankAccountDetails, result.get());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}