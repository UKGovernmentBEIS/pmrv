package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EtsReceivingTransferringInstallation {

    private String etsAccountId;
    private String type;
    private String installationIdentificationCode;
    private String operator;
    private String installationName;
    private String co2source;
}
