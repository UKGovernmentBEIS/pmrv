package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class CalculationStandardReferenceSource {
    
    @Size(max = 500)
    private String otherTypeDetails;
    
    @Size(max = 500)
    private String defaultValue;
}
