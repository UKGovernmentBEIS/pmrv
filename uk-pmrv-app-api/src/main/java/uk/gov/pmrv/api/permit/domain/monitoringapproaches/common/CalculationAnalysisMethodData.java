package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationAnalysisMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#analysisMethodUsed)  == (#analysisMethods?.size() gt 0)}",
    message = "permit.calculationApproach.tier.common.analysisMethodUsed_true.analysisMethods")
public class CalculationAnalysisMethodData {

    private Boolean analysisMethodUsed;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@Valid CalculationAnalysisMethod> analysisMethods = new ArrayList<>();
}
