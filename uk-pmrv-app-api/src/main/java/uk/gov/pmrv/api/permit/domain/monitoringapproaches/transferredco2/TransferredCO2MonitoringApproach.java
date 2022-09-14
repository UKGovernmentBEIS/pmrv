package uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions.AccountingEmissions;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransferredCO2MonitoringApproach extends PermitMonitoringApproachSection {
    
    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<ReceivingTransferringInstallation> receivingTransferringInstallations = new ArrayList<>();

    @Valid
    @NotNull
    private AccountingEmissions accountingEmissions;

    @Valid
    @NotNull
    private ProcedureOptionalForm deductionsToAmountOfTransferredCO2;

    @Valid
    @NotNull
    private ProcedureOptionalForm procedureForLeakageEvents;

    @Valid
    @NotNull
    private TemperaturePressure temperaturePressure;

    @Valid
    @NotNull
    private ProcedureForm transferOfCO2;

    @Valid
    @NotNull
    private ProcedureForm quantificationMethodologies;

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;
    
    @Override
    public Set<UUID> getAttachmentIds() {
        Set<UUID> attachments = new HashSet<>();
        if (accountingEmissions != null 
                && accountingEmissions.getAccountingEmissionsDetails() != null
                && accountingEmissions.getAccountingEmissionsDetails().getHighestRequiredTier() != null
                && accountingEmissions.getAccountingEmissionsDetails().getHighestRequiredTier().getNoHighestRequiredTierJustification() != null) {
            attachments.addAll(accountingEmissions.getAccountingEmissionsDetails().getHighestRequiredTier().getNoHighestRequiredTierJustification().getFiles());
        }
        
        return Collections.unmodifiableSet(attachments);
    }
}
