package uk.gov.pmrv.api.workflow.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.PaymentFeeMethod;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Repository
public interface PaymentFeeMethodRepository extends JpaRepository<PaymentFeeMethod, Long> {

    @Transactional(readOnly = true)
    Optional<PaymentFeeMethod> findByCompetentAuthorityAndRequestType(CompetentAuthority competentAuthority,
                                                                      RequestType requestType);

    @Transactional(readOnly = true)
    Optional<PaymentFeeMethod> findByCompetentAuthorityAndRequestTypeAndType(CompetentAuthority competentAuthority,
                                                                             RequestType requestType, FeeMethodType feeMethodType);

}
