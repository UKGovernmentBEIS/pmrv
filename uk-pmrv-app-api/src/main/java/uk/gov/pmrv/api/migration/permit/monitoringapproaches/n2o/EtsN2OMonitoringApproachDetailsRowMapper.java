package uk.gov.pmrv.api.migration.permit.monitoringapproaches.n2o;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsN2OMonitoringApproachDetailsRowMapper implements RowMapper<EtsN2OMonitoringApproachDetails>{

    @Override
    public EtsN2OMonitoringApproachDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsN2OMonitoringApproachDetails.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .approachDescription(rs.getString("Mn2o_monitoring_of_n2o_description"))

                .emissionDeterminationProcedureDescription(rs.getString("Mn2o_proc_emissions_determination_Procedure_description"))
                .emissionDeterminationProcedureTitle(rs.getString("Mn2o_proc_emissions_determination_Procedure_title"))
                .emissionDeterminationProcedureReference(rs.getString("Mn2o_proc_emissions_determination_Procedure_reference"))
                .emissionDeterminationProcedureDiagramReference(rs.getString("Mn2o_proc_emissions_determination_Procedure_diagram_reference"))
                .emissionDeterminationProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_emissions_determination_Procedure_responsible_post_department"))
                .emissionDeterminationProcedureLocation(rs.getString("Mn2o_proc_emissions_determination_Procedure_location"))
                .emissionDeterminationProcedureItSystem(rs.getString("Mn2o_proc_emissions_determination_Procedure_it_system"))
                .emissionDeterminationProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_emissions_determination_Procedure_cen_or_other_standards_applied"))

                .referencePeriodsProcedureDescription(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_description"))
                .referencePeriodsProcedureTitle(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_title"))
                .referencePeriodsProcedureReference(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_reference"))
                .referencePeriodsProcedureDiagramReference(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_diagram_reference"))
                .referencePeriodsProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_responsible_post_department"))
                .referencePeriodsProcedureLocation(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_location"))
                .referencePeriodsProcedureItSystem(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_it_system"))
                .referencePeriodsProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_reference_periods_substitution_missing_data_Procedure_cen_or_other_standards_applied"))

                .operationalManagementProcedureDescription(rs.getString("Mn2o_proc_operational_management_Procedure_description"))
                .operationalManagementProcedureTitle(rs.getString("Mn2o_proc_operational_management_Procedure_title"))
                .operationalManagementProcedureReference(rs.getString("Mn2o_proc_operational_management_Procedure_reference"))
                .operationalManagementProcedureDiagramReference(rs.getString("Mn2o_proc_operational_management_Procedure_diagram_reference"))
                .operationalManagementProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_operational_management_Procedure_responsible_post_department"))
                .operationalManagementProcedureLocation(rs.getString("Mn2o_proc_operational_management_Procedure_location"))
                .operationalManagementProcedureItSystem(rs.getString("Mn2o_proc_operational_management_Procedure_it_system"))
                .operationalManagementProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_operational_management_Procedure_cen_or_other_standards_applied"))

                .n2oEmissionsProcedureDescription(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_description"))
                .n2oEmissionsProcedureTitle(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_title"))
                .n2oEmissionsProcedureReference(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_reference"))
                .n2oEmissionsProcedureDiagramReference(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_diagram_reference"))
                .n2oEmissionsProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_responsible_post_department"))
                .n2oEmissionsProcedureLocation(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_location"))
                .n2oEmissionsProcedureItSystem(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_it_system"))
                .n2oEmissionsProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_determination_of_n2o_emissions_Procedure_cen_or_other_standards_applied"))

                .n2oConcentrationProcedureDescription(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_description"))
                .n2oConcentrationProcedureTitle(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_title"))
                .n2oConcentrationProcedureReference(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_reference"))
                .n2oConcentrationProcedureDiagramReference(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_diagram_reference"))
                .n2oConcentrationProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_responsible_post_department"))
                .n2oConcentrationProcedureLocation(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_location"))
                .n2oConcentrationProcedureItSystem(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_it_system"))
                .n2oConcentrationProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_determination_of_n2o_concentration_Procedure_cen_or_other_standards_applied"))

                .quantityProductProcedureDescription(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_description"))
                .quantityProductProcedureTitle(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_title"))
                .quantityProductProcedureReference(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_reference"))
                .quantityProductProcedureDiagramReference(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_diagram_reference"))
                .quantityProductProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_responsible_post_department"))
                .quantityProductProcedureLocation(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_location"))
                .quantityProductProcedureItSystem(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_it_system"))
                .quantityProductProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_quantity_of_product_produced_Procedure_cen_or_other_standards_applied"))

                .quantityMaterialsProcedureDescription(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_description"))
                .quantityMaterialsProcedureTitle(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_title"))
                .quantityMaterialsProcedureReference(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_reference"))
                .quantityMaterialsProcedureDiagramReference(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_diagram_reference"))
                .quantityMaterialsProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_responsible_post_department"))
                .quantityMaterialsProcedureLocation(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_location"))
                .quantityMaterialsProcedureItSystem(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_it_system"))
                .quantityMaterialsProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_quantity_of_materials_Procedure_cen_or_other_standards_applied"))

                .gasFlowProcedure("Yes".equals(rs.getString("Mn2o_proc_gas_flow_determined_by_calculation")))
                .gasFlowProcedureDescription(rs.getString("Mn2o_proc_gas_flow_Procedure_description"))
                .gasFlowProcedureTitle(rs.getString("Mn2o_proc_gas_flow_Procedure_title"))
                .gasFlowProcedureReference(rs.getString("Mn2o_proc_gas_flow_Procedure_reference"))
                .gasFlowProcedureDiagramReference(rs.getString("Mn2o_proc_gas_flow_Procedure_diagram_reference"))
                .gasFlowProcedureResponsiblePostDepartment(rs.getString("Mn2o_proc_gas_flow_Procedure_responsible_post_department"))
                .gasFlowProcedureLocation(rs.getString("Mn2o_proc_gas_flow_Procedure_location"))
                .gasFlowProcedureItSystem(rs.getString("Mn2o_proc_gas_flow_Procedure_it_system"))
                .gasFlowProcedureCenOrOtherStandardsApplied(rs.getString("Mn2o_proc_gas_flow_Procedure_cen_or_other_standards_applied"))

                .build();
    }

}