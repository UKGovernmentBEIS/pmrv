package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsCalculationMonitoringApproachDetailsRowMapper implements RowMapper<EtsCalculationMonitoringApproachDetails>{

    @Override
    public EtsCalculationMonitoringApproachDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsCalculationMonitoringApproachDetails.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .approachDescription(rs.getString("Calc_calculation_approach_description"))
                .undertakeSamplingAnalysis("Yes".equals(rs.getString("Cf_undertake_sampling_analysis")))

                .analysesProcedureDescription(rs.getString("Cf_procedures_for_analyses_Procedure_description"))
                .analysesProcedureTitle(rs.getString("Cf_procedures_for_analyses_Procedure_title"))
                .analysesProcedureReference(rs.getString("Cf_procedures_for_analyses_Procedure_reference"))
                .analysesProcedureDiagramReference(rs.getString("Cf_procedures_for_analyses_Procedure_diagram_reference"))
                .analysesProcedureResponsiblePostDepartment(rs.getString("Cf_procedures_for_analyses_Procedure_responsible_post_department"))
                .analysesProcedureLocation(rs.getString("Cf_procedures_for_analyses_Procedure_location"))
                .analysesProcedureItSystem(rs.getString("Cf_procedures_for_analyses_Procedure_it_system"))
                .analysesProcedureCenOrOtherStandardsApplied(rs.getString("Cf_procedures_for_analyses_Procedure_cen_or_other_standards_applied"))

                .samplingPlanProcedureTitle(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_title"))
                .samplingPlanProcedureReference(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_reference"))
                .samplingPlanDiagramReference(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_diagram_reference"))
                .samplingPlanProcedureDescription(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_description"))
                .samplingPlanResponsiblePostDepartment(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_responsible_post_department"))
                .samplingPlanProcedureLocation(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_location"))
                .samplingPlanProcedureItSystem(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_it_system"))
                .samplingPlanProcedureCenOrOtherStandardsApplied(rs.getString("Cf_procedures_analyses_sampling_plan_Procedure_cen_or_other_standards_applied"))

                .appropriatenessProcedureTitle(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_title"))
                .appropriatenessProcedureReference(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_reference"))
                .appropriatenessDiagramReference(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_diagram_reference"))
                .appropriatenessProcedureDescription(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_description"))
                .appropriatenessResponsiblePostDepartment(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_responsible_post_department"))
                .appropriatenessProcedureLocation(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_location"))
                .appropriatenessProcedureItSystem(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_it_system"))
                .appropriatenessProcedureCenOrOtherStandardsApplied(rs.getString("Cf_procedures_revise_sampling_plan_appropriateness_Procedure_cen_or_other_standards_applied"))

                .yearEndReconciliation("Yes".equals(rs.getString("Cf_undertake_sampling_analysis")) && "Yes".equals(rs.getString("Cf_procedures_year_reconciliations")))
                .yearEndReconciliationProcedureTitle(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_title"))
                .yearEndReconciliationProcedureReference(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_reference"))
                .yearEndReconciliationDiagramReference(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_diagram_reference"))
                .yearEndReconciliationProcedureDescription(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_description"))
                .yearEndReconciliationResponsiblePostDepartment(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_responsible_post_department"))
                .yearEndReconciliationProcedureLocation(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_location"))
                .yearEndReconciliationProcedureItSystem(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_it_system"))
                .yearEndReconciliationProcedureCenOrOtherStandardsApplied(rs.getString("Cf_procedures_year_end_reconcilation_Procedure_cen_or_other_standards_applied"))

                .build();
    }

}