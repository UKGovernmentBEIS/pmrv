package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPaymentInfo {

    private LocalDate paymentDate;
    private String paidById;
    private String paidByFullName;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethodType paymentMethod;

    private LocalDate receivedDate;
    private String cancellationReason;
}
