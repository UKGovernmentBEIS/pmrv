package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'OTHER') == (#otherTypeDetails != null)}", 
    message = "permit.calculationApproach.tier.emissionFactor.standardReferenceSource.standardReferenceSourceType.otherDetails")
public class CalculationEmissionFactorStandardReferenceSource extends CalculationStandardReferenceSource {

    @NotNull
    private CalculationEmissionFactorStandardReferenceSourceType type;
    
}
