package uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsFallbackMonitoringApproachDetailsRowMapper implements RowMapper<EtsFallbackMonitoringApproachDetails>{

    @Override
    public EtsFallbackMonitoringApproachDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsFallbackMonitoringApproachDetails.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .approachDescription(rs.getString("Fb_fallback_approach_description"))
                .approachJustification(rs.getString("Fb_fallback_approach_justification"))

                .annualUncertaintyAnalysisProcedureDescription(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_description"))
                .annualUncertaintyAnalysisProcedureTitle(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_title"))
                .annualUncertaintyAnalysisProcedureReference(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_reference"))
                .annualUncertaintyAnalysisProcedureDiagramReference(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_diagram_reference"))
                .annualUncertaintyAnalysisProcedureResponsiblePostDepartment(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_responsible_post_department"))
                .annualUncertaintyAnalysisProcedureLocation(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_location"))
                .annualUncertaintyAnalysisProcedureItSystem(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_it_system"))
                .annualUncertaintyAnalysisProcedureCenOrOtherStandardsApplied(rs.getString("Fb_annual_uncertainty_analysis_procedure_Procedure_cen_or_other_standards_applied"))

                .build();
    }

}