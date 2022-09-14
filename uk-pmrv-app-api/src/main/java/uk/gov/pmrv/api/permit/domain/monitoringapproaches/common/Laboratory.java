package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#laboratoryAccredited) == (#laboratoryAccreditationEvidence == null)}",
    message = "permit.monitoringapproach.common.laboratory.accredited")
public class Laboratory {

    @Size(max = 250)
    @NotBlank
    private String laboratoryName;

    @NotNull
    private Boolean laboratoryAccredited;

    @Size(max = 10000)
    private String laboratoryAccreditationEvidence;
}
