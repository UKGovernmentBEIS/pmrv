package uk.gov.pmrv.api.permit.domain.monitoringapproaches;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CalculationMonitoringApproach.class, name = "CALCULATION"),
    @JsonSubTypes.Type(value = MeasMonitoringApproach.class, name = "MEASUREMENT"),
    @JsonSubTypes.Type(value = FallbackMonitoringApproach.class, name = "FALLBACK"),
    @JsonSubTypes.Type(value = N2OMonitoringApproach.class, name = "N2O"),
    @JsonSubTypes.Type(value = PFCMonitoringApproach.class, name = "PFC"),
    @JsonSubTypes.Type(value = InherentCO2MonitoringApproach.class, name = "INHERENT_CO2"),
    @JsonSubTypes.Type(value = TransferredCO2MonitoringApproach.class, name = "TRANSFERRED_CO2")
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class PermitMonitoringApproachSection implements PermitSection {

    private MonitoringApproachType type;
    
    @JsonIgnore
    public Set<UUID> getAttachmentIds(){
        return Collections.unmodifiableSet(new HashSet<>());
    }
}
