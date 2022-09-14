package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentConfirmRequestTaskPayload extends RequestTaskPayload {

    private String paymentRefNum;
    private LocalDate paymentDate;
    private String paidByFullName;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentMethodType paymentMethod;
}
