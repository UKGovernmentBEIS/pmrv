package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import javax.validation.Valid;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#measurementDevices?.size() gt 0)}", 
    message = "permit.transferredCO2MonitoringApproach.temperaturePressure.exist")
public class TemperaturePressure {

    private boolean exist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MeasurementDevice> measurementDevices;

}
