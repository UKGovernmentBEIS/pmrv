package uk.gov.pmrv.api.workflow.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class PaymentFeeMethodRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private PaymentFeeMethodRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByCompetentAuthorityAndRequestType() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestType(competentAuthority, requestType);

        assertThat(optionalResult).isNotEmpty();
        assertEquals(paymentFeeMethod, optionalResult.get());
    }

    @Test
    void findByCompetentAuthorityAndRequestType_no_result() {
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(CompetentAuthority.SCOTLAND)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestType(CompetentAuthority.WALES, requestType);

        assertThat(optionalResult).isEmpty();
    }

    @Test
    void findByCompetentAuthorityAndRequestTypeAndType() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        FeeMethodType feeMethodType = FeeMethodType.STANDARD;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(feeMethodType)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, feeMethodType);

        assertThat(optionalResult).isNotEmpty();
        assertEquals(paymentFeeMethod, optionalResult.get());
    }

    @Test
    void findByCompetentAuthorityAndRequestTypeAndType_no_result() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        PaymentFeeMethod paymentFeeMethod = PaymentFeeMethod.builder()
            .competentAuthority(competentAuthority)
            .requestType(requestType)
            .type(FeeMethodType.STANDARD)
            .build();

        entityManager.persist(paymentFeeMethod);

        flushAndClear();

        Optional<PaymentFeeMethod> optionalResult =
            repository.findByCompetentAuthorityAndRequestTypeAndType(competentAuthority, requestType, FeeMethodType.INSTALLATION_CATEGORY_BASED);

        assertThat(optionalResult).isEmpty();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}