package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2;

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
public class ReceivingTransferringInstallation {

    @NotNull
    private ReceivingTransferringInstallationType type;
    
    @NotBlank
    @Size(max=10000)
    private String installationIdentificationCode;
    
    @NotBlank
    @Size(max=10000)
    private String operator;
    
    @NotBlank
    @Size(max=10000)
    private String installationName;
    
    @NotBlank
    @Size(max=10000)
    private String co2source;
}
