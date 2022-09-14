package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.SourceStreamCategoryBase;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationSourceStreamCategory extends SourceStreamCategoryBase {

    @NotNull
    private CalculationMethod calculationMethod;
}
