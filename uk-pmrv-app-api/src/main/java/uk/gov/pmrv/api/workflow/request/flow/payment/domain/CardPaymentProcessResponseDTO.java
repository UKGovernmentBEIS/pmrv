package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardPaymentProcessResponseDTO {

    private String paymentId;
    private CardPaymentStateDTO state;
    private String nextUrl;
}
