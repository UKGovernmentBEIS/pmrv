package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#type eq 'OTHER') == (#otherTypeName != null)}", 
    message = "permit.transferredCO2MonitoringApproach.measurementDevice.otherTypeName")
public class MeasurementDevice {

    @Size(max = 10000)
    @NotBlank
    private String reference;

    @NotNull
    private MeasurementDeviceType type;

    @Size(max = 10000)
    private String otherTypeName;

    @Size(max = 10000)
    @NotBlank
    private String location;

}
