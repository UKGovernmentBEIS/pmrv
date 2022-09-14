package uk.gov.pmrv.api.workflow.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGetInfo {

    private String paymentId;
    private CompetentAuthority competentAuthority;
}
