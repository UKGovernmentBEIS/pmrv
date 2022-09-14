package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardPaymentStateDTO {

    private String status;
    private boolean finished;
    private String code;
    private String message;
}
