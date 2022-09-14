package uk.gov.pmrv.api.migration.permit.measurementdevices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsMeteringMeasurementDevice {

    private String emitterId;

    private String reference;

    private String type;

    private String range;

    private String rangeUnits;

    private String uncertainty;

    private String location;
}
