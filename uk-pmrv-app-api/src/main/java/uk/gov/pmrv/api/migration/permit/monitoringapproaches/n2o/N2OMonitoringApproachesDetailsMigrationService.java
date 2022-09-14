package uk.gov.pmrv.api.migration.permit.monitoringapproaches.n2o;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class N2OMonitoringApproachesDetailsMigrationService implements PermitSectionMigrationService<N2OMonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE  =
            "with XMLNAMESPACES (\r\n" +
                    "'urn:www-toplev-com:officeformsofd' AS fd\r\n" +
                    "), allPermits as (\r\n" +
                    "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID,\r\n" +
                    "           FD.fldMajorVersion versionKey\r\n" +
                    "           --, format(P.fldDisplayID, '00') + '|' + format(FD.fldMajorVersion, '0000') + '|' + format(FD.fldMinorVersion, '0000')  versionKey\r\n" +
                    "      from tblForm F\r\n" +
                    "      join tblFormData FD        on FD.fldFormID = F.fldFormID\r\n" +
                    "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n" +
                    "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\r\n" +
                    "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\r\n" +
                    "     where P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\r\n" +
                    "), latestVersion as (\r\n" +
                    "    select fldEmitterID, max(versionKey) MaxVersionKey from allPermits group by fldEmitterID\r\n" +
                    "), latestPermit as (\r\n" +
                    "    select p.fldEmitterID, FD.*\r\n" +
                    "  from allPermits p\r\n" +
                    "  join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.fldMajorVersion = v.MaxVersionKey and p.fldMinorVersion = 0 \r\n" +
                    "  join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\r\n" +
                    "  join tblEmitter E   on E.fldEmitterID = p.fldEmitterID\r\n" +
                    "  join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live'\r\n" +
                    ")\r\n" +
                    "select E.fldEmitterID,\r\n" +
                    "-- Approach Description\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_monitoring_of_n2o_description)[1]', 'NVARCHAR(max)'),'')) Mn2o_monitoring_of_n2o_description,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_emissions_determination-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_emissions_determination_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Determination of Reference Periods\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_reference_periods_substitution_missing_data-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_reference_periods_substitution_missing_data_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Operational Management\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_operational_management-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_operational_management_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Determination of N2O Emissions\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_emissions-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_emissions_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Determination of N2O Concentration\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_determination_of_n2o_concentration-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_determination_of_n2o_concentration_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Determination of the Quantity of Product Produced\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_product_produced-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_product_produced_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Quantity of Materials\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_quantity_of_materials-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_quantity_of_materials_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Gas Flow Determination\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow_determined_by_calculation)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_determined_by_calculation,\r\n" +
                    "-- Gas Flow Determination procedure - If Mn2o_proc_gas_flow_determined_by_calculation = 'Yes'\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_gas_flow-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mn2o_proc_gas_flow_Procedure_cen_or_other_standards_applied--,\r\n" +
                    "-- -- Deviation from Normal Operations\r\n" +
                    "-- trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mn2o_proc_deviation_from_normal_operations)[1]', 'NVARCHAR(max)')) Mn2o_proc_deviation_from_normal_operations\r\n" +
                    "            --, fldSubmittedXML\r\n" +
                    "  from tblEmitter E\r\n" +
                    "     join latestPermit F on E.fldEmitterID = F.fldEmitterID\r\n" +
                    "where fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_n2o)[1]', 'NVARCHAR(max)') = 'Yes'\r\n"
            ;

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, N2OMonitoringApproach> sections = queryEtsSection(accountIds);

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            N2OMonitoringApproach n2OMonitoringApproach =
                    (N2OMonitoringApproach)permitMigrationContainer
                    .getPermitContainer()
                    .getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(MonitoringApproachType.N2O);
            n2OMonitoringApproach.setApproachDescription(section.getApproachDescription());
            n2OMonitoringApproach.setEmissionDetermination(section.getEmissionDetermination());
            n2OMonitoringApproach.setReferenceDetermination(section.getReferenceDetermination());
            n2OMonitoringApproach.setOperationalManagement(section.getOperationalManagement());
            n2OMonitoringApproach.setNitrousOxideEmissionsDetermination(section.getNitrousOxideEmissionsDetermination());
            n2OMonitoringApproach.setNitrousOxideConcentrationDetermination(section.getNitrousOxideConcentrationDetermination());
            n2OMonitoringApproach.setQuantityProductDetermination(section.getQuantityProductDetermination());
            n2OMonitoringApproach.setQuantityMaterials(section.getQuantityMaterials());
            n2OMonitoringApproach.setGasFlowCalculation(section.getGasFlowCalculation());
        });
    }

    @Override
    public Map<String, N2OMonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("and e.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();

        Map<String, EtsN2OMonitoringApproachDetails> etsN2OMonitoringApproachDetailsMap = executeQuery(query, accountIds);

        Map<String, N2OMonitoringApproach> n2oMonitoringApproaches = new HashMap<>();
        etsN2OMonitoringApproachDetailsMap.forEach((etsAccountId, etsN2OMonitoringApproachDetails) -> {
            String approachDescription = N2OApproachDetailsMapper.constructN2OApproachDescription(etsN2OMonitoringApproachDetails);
            ProcedureForm emissionDetermination = N2OApproachDetailsMapper.constructEmissionDeterminationProcedure(etsN2OMonitoringApproachDetails);
            ProcedureForm referenceDetermination = N2OApproachDetailsMapper.constructReferencePeriodsProcedure(etsN2OMonitoringApproachDetails);
            ProcedureForm operationalManagement = N2OApproachDetailsMapper.constructOperationalManagementProcedure(etsN2OMonitoringApproachDetails);
            ProcedureForm nitrousOxideEmissionsDetermination = N2OApproachDetailsMapper.constructN2oEmissionsProcedure(etsN2OMonitoringApproachDetails);
            ProcedureForm nitrousOxideConcentrationDetermination = N2OApproachDetailsMapper.constructN2oConcentrationProcedure(etsN2OMonitoringApproachDetails);
            ProcedureForm quantityProductDetermination = N2OApproachDetailsMapper.constructQuantityProductProcedure(etsN2OMonitoringApproachDetails);
            ProcedureForm quantityMaterials = N2OApproachDetailsMapper.constructQuantityMaterialsProcedure(etsN2OMonitoringApproachDetails);
            ProcedureOptionalForm gasFlowCalculation = N2OApproachDetailsMapper.constructGasFlowProcedureProcedure(etsN2OMonitoringApproachDetails);

            n2oMonitoringApproaches.put(etsAccountId,
                    N2OMonitoringApproach.builder()
                            .approachDescription(approachDescription)
                            .emissionDetermination(emissionDetermination)
                            .referenceDetermination(referenceDetermination)
                            .operationalManagement(operationalManagement)
                            .nitrousOxideEmissionsDetermination(nitrousOxideEmissionsDetermination)
                            .nitrousOxideConcentrationDetermination(nitrousOxideConcentrationDetermination)
                            .quantityProductDetermination(quantityProductDetermination)
                            .quantityMaterials(quantityMaterials)
                            .gasFlowCalculation(gasFlowCalculation)
                            .build());
        });
        return n2oMonitoringApproaches;
    }

    private Map<String, EtsN2OMonitoringApproachDetails> executeQuery(String query, List<String> accountIds) {
        List<EtsN2OMonitoringApproachDetails> etsN2OMonitoringApproachesDetails = migrationJdbcTemplate.query(query,
                new EtsN2OMonitoringApproachDetailsRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsN2OMonitoringApproachesDetails
                .stream()
                .collect(Collectors.toMap(EtsN2OMonitoringApproachDetails::getEtsAccountId,
                        etsN2OMonitoringApproachDetails -> etsN2OMonitoringApproachDetails));
    }
}
