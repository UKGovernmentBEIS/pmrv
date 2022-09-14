package uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.details;

import org.springframework.jdbc.core.RowMapper;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.details.EtsPFCMonitoringApproachDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsPFCMonitoringApproachDetailsRowMapper implements RowMapper<EtsPFCMonitoringApproachDetails>{

    @Override
    public EtsPFCMonitoringApproachDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsPFCMonitoringApproachDetails.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .approachDescription(rs.getString("Mpfc_monitoring_of_pfc_description"))

                .collectionEfficiencyProcedureDescription(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_description"))
                .collectionEfficiencyProcedureTitle(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_title"))
                .collectionEfficiencyProcedureReference(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_reference"))
                .collectionEfficiencyProcedureDiagramReference(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_diagram_reference"))
                .collectionEfficiencyProcedureResponsiblePostDepartment(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_responsible_post_department"))
                .collectionEfficiencyProcedureLocation(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_location"))
                .collectionEfficiencyProcedureItSystem(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_it_system"))
                .collectionEfficiencyProcedureCenOrOtherStandardsApplied(rs.getString("Mpfc_proc_fugitive_emissions_Procedure_cen_or_other_standards_applied"))

                .tier2EmissionFactor("Yes".equals(rs.getString("Mpfc_tier_2_ef_applied")))

                .determinationOfEmissionFactorsProcedureDescription(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_description"))
                .determinationOfEmissionFactorsProcedureTitle(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_title"))
                .determinationOfEmissionFactorsProcedureReference(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_reference"))
                .determinationOfEmissionFactorsProcedureDiagramReference(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_diagram_reference"))
                .determinationOfEmissionFactorsProcedureResponsiblePostDepartment(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_responsible_post_department"))
                .determinationOfEmissionFactorsProcedureLocation(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_location"))
                .determinationOfEmissionFactorsProcedureItSystem(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_it_system"))
                .determinationOfEmissionFactorsProcedureCenOrOtherStandardsApplied(rs.getString("Mpfc_proc_determination_of_emission_factors_Procedure_cen_or_other_standards_applied"))

                .scheduleOfMeasurementsProcedureDescription(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_description"))
                .scheduleOfMeasurementsProcedureTitle(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_title"))
                .scheduleOfMeasurementsProcedureReference(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_reference"))
                .scheduleOfMeasurementsProcedureDiagramReference(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_diagram_reference"))
                .scheduleOfMeasurementsProcedureResponsiblePostDepartment(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_responsible_post_department"))
                .scheduleOfMeasurementsProcedureLocation(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_location"))
                .scheduleOfMeasurementsProcedureItSystem(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_it_system"))
                .scheduleOfMeasurementsProcedureCenOrOtherStandardsApplied(rs.getString("Mpfc_proc_schedule_of_measurements_Procedure_cen_or_other_standards_applied"))

                .build();
    }

}