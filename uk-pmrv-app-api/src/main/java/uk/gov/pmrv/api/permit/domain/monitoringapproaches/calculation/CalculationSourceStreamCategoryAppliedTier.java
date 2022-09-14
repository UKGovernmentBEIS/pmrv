package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationSourceStreamCategoryAppliedTier {

    @Valid
    @NotNull
    private CalculationSourceStreamCategory sourceStreamCategory;
    
    @Valid
    @NotNull
    private CalculationEmissionFactor emissionFactor;
    
    @Valid
    @NotNull
    private CalculationOxidationFactor oxidationFactor;

    @Valid
    @NotNull
    private CalculationActivityData activityData;

    @Valid
    @NotNull
    private CalculationCarbonContent carbonContent;

    @Valid
    @NotNull
    private CalculationNetCalorificValue netCalorificValue;

    @Valid
    @NotNull
    private CalculationConversionFactor conversionFactor;
    
    @Valid
    @NotNull
    private CalculationBiomassFraction biomassFraction;
}
