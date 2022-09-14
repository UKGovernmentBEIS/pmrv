package uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;

import javax.validation.Valid;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "#exist == (#determinationInstallation != null)",
        message = "permit.pfcMonitoringApproach.pfcTier2EmissionFactor.determinationInstallation")
@SpELExpression(expression = "#exist == (#scheduleMeasurements != null)",
        message = "permit.pfcMonitoringApproach.pfcTier2EmissionFactor.scheduleMeasurements")
public class PFCTier2EmissionFactor {

    private boolean exist;

    @Valid
    private ProcedureForm determinationInstallation;

    @Valid
    private ProcedureForm scheduleMeasurements;
}
