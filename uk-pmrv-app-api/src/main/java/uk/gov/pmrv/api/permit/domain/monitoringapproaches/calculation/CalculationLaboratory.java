package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationLaboratory {
    
    @Size(max = 250)
    @NotBlank
    private String laboratoryName;

    @NotNull
    private Boolean laboratoryAccredited;
}
