package uk.gov.pmrv.api.workflow.request.flow.payment.domain;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.payment.domain.dto.BankAccountDetailsDTO;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentMakeRequestTaskPayload extends RequestTaskPayload {

    private BigDecimal amount;

    private String paymentRefNum;

    private LocalDate creationDate;

    private Set<PaymentMethodType> paymentMethodTypes;

    private BankAccountDetailsDTO bankAccountDetails;

    private String externalPaymentId;
}
