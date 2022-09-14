package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsTransferredCO2MonitoringApproachRowMapper implements RowMapper<EtsTransferredCO2MonitoringApproach> {

    @Override
    public EtsTransferredCO2MonitoringApproach mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsTransferredCO2MonitoringApproach.builder()
                .etsAccountId(rs.getString("fldEmitterID"))

                .deductionsToAmountExist(rs.getString("Mtico2_deductions_of_transferred_co2").trim().equalsIgnoreCase("Yes"))
                .deductionsToAmountProcedureDescription(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_description"))
                .deductionsToAmountProcedureDocumentName(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_title"))
                .deductionsToAmountProcedureReference(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_reference"))
                .deductionsToAmountDiagramReference(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_diagram_reference"))
                .deductionsToAmountResponsibleDepartmentOrRole(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_responsible_post_department"))
                .deductionsToAmountLocationOfRecords(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_location"))
                .deductionsToAmountItSystemUsed(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_it_system"))
                .deductionsToAmountAppliedStandards(rs.getString("Mtico2_proc_deductions_of_transferred_co2_Procedure_cen_or_other_standards_applied"))

                .leakageEventsExist(rs.getString("Mtico2_leakage_events").equalsIgnoreCase("Yes"))
                .leakageEventsProcedureDescription(rs.getString("Mtico2_proc_leakage_events_Procedure_description"))
                .leakageEventsProcedureDocumentName(rs.getString("Mtico2_proc_leakage_events_Procedure_title"))
                .leakageEventsProcedureReference(rs.getString("Mtico2_proc_leakage_events_Procedure_reference"))
                .leakageEventsDiagramReference(rs.getString("Mtico2_proc_leakage_events_Procedure_diagram_reference"))
                .leakageEventsResponsibleDepartmentOrRole(rs.getString("Mtico2_proc_leakage_events_Procedure_responsible_post_department"))
                .leakageEventsLocationOfRecords(rs.getString("Mtico2_proc_leakage_events_Procedure_location"))
                .leakageEventsItSystemUsed(rs.getString("Mtico2_proc_leakage_events_Procedure_it_system"))
                .leakageEventsAppliedStandards(rs.getString("Mtico2_proc_leakage_events_Procedure_cen_or_other_standards_applied"))

                .temperaturePressureExist(rs.getString("Mtico2_temp_pressure_equipment_used").equalsIgnoreCase("Yes"))

                .transferOfCO2ProcedureDescription(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_description"))
                .transferOfCO2ProcedureDocumentName(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_title"))
                .transferOfCO2ProcedureReference(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_reference"))
                .transferOfCO2DiagramReference(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_diagram_reference"))
                .transferOfCO2ResponsibleDepartmentOrRole(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_responsible_post_department"))
                .transferOfCO2LocationOfRecords(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_location"))
                .transferOfCO2ItSystemUsed(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_it_system"))
                .transferOfCO2AppliedStandards(rs.getString("Mtico2_proc_transfer_of_co2_Procedure_cen_or_other_standards_applied"))

                .quantificationMethodologiesProcedureDescription(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_description"))
                .quantificationMethodologiesProcedureDocumentName(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_title"))
                .quantificationMethodologiesProcedureReference(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_reference"))
                .quantificationMethodologiesDiagramReference(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_diagram_reference"))
                .quantificationMethodologiesResponsibleDepartmentOrRole(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_responsible_post_department"))
                .quantificationMethodologiesLocationOfRecords(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_location"))
                .quantificationMethodologiesItSystemUsed(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_it_system"))
                .quantificationMethodologiesAppliedStandards(rs.getString("Mtico2_proc_quantification_methodologies_Procedure_cen_or_other_standards_applied"))

                .approachDescription(rs.getString("Mtico2_monitoring_of_transferred_inherent_co2_description"))

                .build();
    }
}
