package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardPaymentCreateResponseDTO {

    private Boolean pendingPaymentExist;
    private String nextUrl;
}
