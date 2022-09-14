package uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CellAndAnodeType {

    @NotBlank
    //TODO revisit validation
    @Size(max = 100)
    private String cellType;

    @NotBlank
    //TODO revisit validation
    @Size(max = 100)
    private String anodeType;
}
