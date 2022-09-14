package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentMarkAsReceivedRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    @PastOrPresent
    private LocalDate receivedDate;
}
