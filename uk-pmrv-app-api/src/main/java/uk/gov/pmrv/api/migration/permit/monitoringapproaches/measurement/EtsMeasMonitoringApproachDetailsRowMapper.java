package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurement;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsMeasMonitoringApproachDetailsRowMapper implements RowMapper<EtsMeasMonitoringApproachDetails> {
    @Override
    public EtsMeasMonitoringApproachDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsMeasMonitoringApproachDetails.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .approachDescription(rs.getString("Meas_measurment_approach_description"))

                .emissionDeterminationProcedureTitle(rs.getString("Meas_proc_emissions_determination_Procedure_title"))
                .emissionDeterminationProcedureReference(rs.getString("Meas_proc_emissions_determination_Procedure_reference"))
                .emissionDeterminationProcedureDiagramReference(rs.getString("Meas_proc_emissions_determination_Procedure_diagram_reference"))
                .emissionDeterminationProcedureDescription(rs.getString("Meas_proc_emissions_determination_Procedure_description"))
                .emissionDeterminationProcedureResponsiblePostDepartment(rs.getString("Meas_proc_emissions_determination_Procedure_responsible_post_department"))
                .emissionDeterminationProcedureLocation(rs.getString("Meas_proc_emissions_determination_Procedure_location"))
                .emissionDeterminationProcedureItSystem(rs.getString("Meas_proc_emissions_determination_Procedure_it_system"))
                .emissionDeterminationProcedureCenOrOtherStandardsApplied(rs.getString("Meas_proc_emissions_determination_Procedure_cen_or_other_standards_applied"))

                .referencePeriodDeterminationProcedureTitle(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_title"))
                .referencePeriodDeterminationProcedureReference(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_reference"))
                .referencePeriodDeterminationProcedureDiagramReference(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_diagram_reference"))
                .referencePeriodDeterminationProcedureDescription(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_description"))
                .referencePeriodDeterminationProcedureResponsiblePostDepartment(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_responsible_post_department"))
                .referencePeriodDeterminationProcedureLocation(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_location"))
                .referencePeriodDeterminationProcedureItSystem(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_it_system"))
                .referencePeriodDeterminationProcedureCenOrOtherStandardsApplied(rs.getString("Meas_proc_reference_periods_substitution_missing_data_Procedure_cen_or_other_standards_applied"))

                .gasFlowCalculation("Yes".equals(rs.getString("Meas_proc_gas_flow_determined_by_calculation")))
                .gasFlowCalculationProcedureTitle(rs.getString("Meas_proc_gas_flow_Procedure_title"))
                .gasFlowCalculationProcedureReference(rs.getString("Meas_proc_gas_flow_Procedure_reference"))
                .gasFlowCalculationProcedureDiagramReference(rs.getString("Meas_proc_gas_flow_Procedure_diagram_reference"))
                .gasFlowCalculationProcedureDescription(rs.getString("Meas_proc_gas_flow_Procedure_description"))
                .gasFlowCalculationProcedureResponsiblePostDepartment(rs.getString("Meas_proc_gas_flow_Procedure_responsible_post_department"))
                .gasFlowCalculationProcedureLocation(rs.getString("Meas_proc_gas_flow_Procedure_location"))
                .gasFlowCalculationProcedureItSystem(rs.getString("Meas_proc_gas_flow_Procedure_it_system"))
                .gasFlowCalculationProcedureCenOrOtherStandardsApplied(rs.getString("Meas_proc_gas_flow_Procedure_cen_or_other_standards_applied"))

                .biomassEmissionsCalculation("Yes".equals(rs.getString("Meas_proc_biomass_emissions_deducted")))
                .biomassEmissionsProcedureTitle(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_title"))
                .biomassEmissionsProcedureReference(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_reference"))
                .biomassEmissionsProcedureDiagramReference(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_diagram_reference"))
                .biomassEmissionsProcedureDescription(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_description"))
                .biomassEmissionsProcedureResponsiblePostDepartment(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_responsible_post_department"))
                .biomassEmissionsProcedureLocation(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_location"))
                .biomassEmissionsProcedureItSystem(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_it_system"))
                .biomassEmissionsProcedureCenOrOtherStandardsApplied(rs.getString("Meas_proc_deduction_of_biomass_emissions_Procedure_cen_or_other_standards_applied"))

                .corroboratingCalculationsProcedureTitle(rs.getString("Meas_proc_corroborating_calculations_Procedure_title"))
                .corroboratingCalculationsProcedureReference(rs.getString("Meas_proc_corroborating_calculations_Procedure_reference"))
                .corroboratingCalculationsProcedureDiagramReference(rs.getString("Meas_proc_corroborating_calculations_Procedure_diagram_reference"))
                .corroboratingCalculationsProcedureDescription(rs.getString("Meas_proc_corroborating_calculations_Procedure_description"))
                .corroboratingCalculationsProcedureResponsiblePostDepartment(rs.getString("Meas_proc_corroborating_calculations_Procedure_responsible_post_department"))
                .corroboratingCalculationsProcedureLocation(rs.getString("Meas_proc_corroborating_calculations_Procedure_location"))
                .corroboratingCalculationsProcedureItSystem(rs.getString("Meas_proc_corroborating_calculations_Procedure_it_system"))
                .corroboratingCalculationsProcedureCenOrOtherStandardsApplied(rs.getString("Meas_proc_corroborating_calculations_Procedure_cen_or_other_standards_applied"))

                .build();

    }
}
