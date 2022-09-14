package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsTemperaturePressure {

    private String etsAccountId;
    private String reference;
    private String type;
    private String location;
}
