package uk.gov.pmrv.api.workflow.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateResult {

    private String paymentId;
    private String nextUrl;
}
