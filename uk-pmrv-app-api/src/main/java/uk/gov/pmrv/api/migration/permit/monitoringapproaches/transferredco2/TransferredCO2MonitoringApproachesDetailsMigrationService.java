package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TransferredCO2MonitoringApproachesDetailsMigrationService implements PermitSectionMigrationService<TransferredCO2MonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE =
            "with XMLNAMESPACES ( \n" +
                    " 'urn:www-toplev-com:officeformsofd' AS fd \n" +
                    "), allPermits as ( \n" +
                    " select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, \n" +
                    "  FD.fldMajorVersion versionKey \n" +
                    " from tblForm F \n" +
                    " join tblFormData FD        on FD.fldFormID = F.fldFormID \n" +
                    " join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID \n" +
                    " join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID \n" +
                    " join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID \n" +
                    " where P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication' \n" +
                    "), latestVersion as ( \n" +
                    " select fldEmitterID, max(versionKey) MaxVersionKey from allPermits group by fldEmitterID \n" +
                    "), latestPermit as ( \n" +
                    " select p.fldEmitterID, FD.* \n" +
                    " from allPermits p \n" +
                    " join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.fldMajorVersion = v.MaxVersionKey and p.fldMinorVersion = 0  \n" +
                    " join tblFormData FD on FD.fldFormDataID = p.fldFormDataID \n" +
                    " join tblEmitter E   on E.fldEmitterID = p.fldEmitterID \n" +
                    " join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \n" +
                    "), a as ( \n" +
                    " select fldEmitterID, \n" +
                    " -- Approach Description \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_monitoring_of_transferred_inherent_co2_description)[1]', 'NVARCHAR(max)')) Mtico2_monitoring_of_transferred_inherent_co2_description, \n" +
                    " -- Deductions to the Amount of Transferred CO2 \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_deductions_of_transferred_co2                                              )[1]', 'NVARCHAR(max)')) Mtico2_deductions_of_transferred_co2, \n" +
                    " -- Procedure - if Mtico2_deductions_of_transferred_co2 = 'Yes' \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_title                         )[1]', 'NVARCHAR(max)')) Mtico2_proc_deductions_of_transferred_co2_Procedure_title                         , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_reference                     )[1]', 'NVARCHAR(max)')) Mtico2_proc_deductions_of_transferred_co2_Procedure_reference                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_deductions_of_transferred_co2_Procedure_diagram_reference             , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_description                   )[1]', 'NVARCHAR(max)')) Mtico2_proc_deductions_of_transferred_co2_Procedure_description                   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)')) Mtico2_proc_deductions_of_transferred_co2_Procedure_responsible_post_department   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_location                      )[1]', 'NVARCHAR(max)')) Mtico2_proc_deductions_of_transferred_co2_Procedure_location                      , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_deductions_of_transferred_co2_Procedure_it_system                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_deductions_of_transferred_co2-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_deductions_of_transferred_co2_Procedure_cen_or_other_standards_applied, \n" +
                    " -- Preventing, Detecting and Quantification of Leakage \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_leakage_events                                              )[1]', 'NVARCHAR(max)')) Mtico2_leakage_events, \n" +
                    " -- Procedure - If Mtico2_leakage_events = 'Yes' \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_title                         )[1]', 'NVARCHAR(max)')) Mtico2_proc_leakage_events_Procedure_title                         , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_reference                     )[1]', 'NVARCHAR(max)')) Mtico2_proc_leakage_events_Procedure_reference                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_leakage_events_Procedure_diagram_reference             , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_description                   )[1]', 'NVARCHAR(max)')) Mtico2_proc_leakage_events_Procedure_description                   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)')) Mtico2_proc_leakage_events_Procedure_responsible_post_department   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_location                      )[1]', 'NVARCHAR(max)')) Mtico2_proc_leakage_events_Procedure_location                      , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_leakage_events_Procedure_it_system                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_leakage_events-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_leakage_events_Procedure_cen_or_other_standards_applied, \n" +
                    " -- Transfer of CO2 \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_title                         )[1]', 'NVARCHAR(max)')) Mtico2_proc_transfer_of_co2_Procedure_title                         , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_reference                     )[1]', 'NVARCHAR(max)')) Mtico2_proc_transfer_of_co2_Procedure_reference                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_transfer_of_co2_Procedure_diagram_reference             , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_description                   )[1]', 'NVARCHAR(max)')) Mtico2_proc_transfer_of_co2_Procedure_description                   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)')) Mtico2_proc_transfer_of_co2_Procedure_responsible_post_department   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_location                      )[1]', 'NVARCHAR(max)')) Mtico2_proc_transfer_of_co2_Procedure_location                      , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_transfer_of_co2_Procedure_it_system                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_transfer_of_co2-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_transfer_of_co2_Procedure_cen_or_other_standards_applied, \n" +
                    " -- Quantification Methodologies \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_title                         )[1]', 'NVARCHAR(max)')) Mtico2_proc_quantification_methodologies_Procedure_title                         , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_reference                     )[1]', 'NVARCHAR(max)')) Mtico2_proc_quantification_methodologies_Procedure_reference                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_quantification_methodologies_Procedure_diagram_reference             , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_description                   )[1]', 'NVARCHAR(max)')) Mtico2_proc_quantification_methodologies_Procedure_description                   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)')) Mtico2_proc_quantification_methodologies_Procedure_responsible_post_department   , \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_location                      )[1]', 'NVARCHAR(max)')) Mtico2_proc_quantification_methodologies_Procedure_location                      , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_quantification_methodologies_Procedure_it_system                     , \n" +
                    " trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_proc_quantification_methodologies-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mtico2_proc_quantification_methodologies_Procedure_cen_or_other_standards_applied, \n" +
                    " -- Temperature Pressure \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_temp_pressure_equipment_used                                              )[1]', 'NVARCHAR(max)')) Mtico2_temp_pressure_equipment_used \n" +
                    " from latestPermit \n" +
                    " where fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_transferred_inherent_co2)[1]', 'NVARCHAR(max)') = 'Yes' \n" +
                    ") \n" +
                    "select * from a ";

    private static final String INSTALLATION_QUERY =
            "with XMLNAMESPACES (\n" +
                    "\t'urn:www-toplev-com:officeformsofd' AS fd\n" +
                    "), allPermits as (\n" +
                    "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID,\n" +
                    "           FD.fldMajorVersion versionKey\n" +
                    "           --, format(P.fldDisplayID, '00') + '|' + format(FD.fldMajorVersion, '0000') + '|' + format(FD.fldMinorVersion, '0000')  versionKey\n" +
                    "      from tblForm F\n" +
                    "      join tblFormData FD        on FD.fldFormID = F.fldFormID\n" +
                    "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\n" +
                    "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\n" +
                    "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\n" +
                    "     where P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\n" +
                    "), latestVersion as (\n" +
                    "    select fldEmitterID, max(versionKey) MaxVersionKey from allPermits group by fldEmitterID\n" +
                    "), latestPermit as (\n" +
                    "    select p.fldEmitterID, FD.*\n" +
                    "\t  from allPermits p\n" +
                    "\t  join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.fldMajorVersion = v.MaxVersionKey and p.fldMinorVersion = 0 \n" +
                    "\t  join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\n" +
                    "\t  join tblEmitter E   on E.fldEmitterID = p.fldEmitterID\n" +
                    "\t  join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live'\n" +
                    "), a as (\n" +
                    "\tselect\tfldEmitterID,\n" +
                    "\t\t\t-- Mtico2_receiving_and_transferring_installations: 'Transferring installation'/'Receiving installation'\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_transferring_or_receiving_installation  ').value('.', 'NVARCHAR(MAX)')) Mtico2_transferring_or_receiving_installation  ,\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_installation_identification_code        ').value('.', 'NVARCHAR(MAX)')) Mtico2_installation_identification_code        ,\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_operator                                ').value('.', 'NVARCHAR(MAX)')) Mtico2_operator                                ,\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_installation_name                       ').value('.', 'NVARCHAR(MAX)')) Mtico2_installation_name                       ,\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_source_of_co2                           ').value('.', 'NVARCHAR(MAX)')) Mtico2_source_of_co2                         \n" +
                    "\t from latestPermit\n" +
                    "\tcross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Mtico2_receiving_and_transferring_installations/row)') as T(c)\n" +
                    "\twhere fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_transferred_inherent_co2)[1]', 'NVARCHAR(max)') = 'Yes'\n" +
                    ")\n" +
                    "select * from a ";

    private static final String TEMPERATURE_QUERY =
            "with XMLNAMESPACES (\n" +
                    "\t'urn:www-toplev-com:officeformsofd' AS fd\n" +
                    "), allPermits as (\n" +
                    "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID,\n" +
                    "           FD.fldMajorVersion versionKey\n" +
                    "           --, format(P.fldDisplayID, '00') + '|' + format(FD.fldMajorVersion, '0000') + '|' + format(FD.fldMinorVersion, '0000')  versionKey\n" +
                    "      from tblForm F\n" +
                    "      join tblFormData FD        on FD.fldFormID = F.fldFormID\n" +
                    "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\n" +
                    "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\n" +
                    "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\n" +
                    "     where P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\n" +
                    "), latestVersion as (\n" +
                    "    select fldEmitterID, max(versionKey) MaxVersionKey from allPermits group by fldEmitterID\n" +
                    "), latestPermit as (\n" +
                    "    select p.fldEmitterID, FD.*\n" +
                    "\t  from allPermits p\n" +
                    "\t  join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.fldMajorVersion = v.MaxVersionKey and p.fldMinorVersion = 0 \n" +
                    "\t  join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\n" +
                    "\t  join tblEmitter E   on E.fldEmitterID = p.fldEmitterID\n" +
                    "\t  join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live'\n" +
                    "), a as (\n" +
                    "\tselect\tfldEmitterID,\n" +
                    "\t\t\t-- Mtico2_temp_pressure_equipment\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_measurement_device_ref    ').value('.', 'NVARCHAR(MAX)')) Mtico2_measurement_device_ref,\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_type_of_measurement_device').value('.', 'NVARCHAR(MAX)')) Mtico2_type_of_measurement_device,\n" +
                    "\t\t\ttrim(T.c.query('Mtico2_device_location           ').value('.', 'NVARCHAR(MAX)')) Mtico2_device_location\n" +
                    "            --, fldSubmittedXML\n" +
                    "\t from latestPermit\n" +
                    "\tcross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Mtico2_temp_pressure_equipment/row)') as T(c)\n" +
                    "\twhere fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_transferred_inherent_co2)[1]', 'NVARCHAR(max)') = 'Yes'\n" +
                    ")\n" +
                    "select * from a ";

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, TransferredCO2MonitoringApproach> sections = queryEtsSection(accountIds);

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            TransferredCO2MonitoringApproach transferredCO2Approach =
                    (TransferredCO2MonitoringApproach) permitMigrationContainer
                            .getPermitContainer()
                            .getPermit()
                            .getMonitoringApproaches()
                            .getMonitoringApproaches()
                            .get(MonitoringApproachType.TRANSFERRED_CO2);

            transferredCO2Approach.setReceivingTransferringInstallations(section.getReceivingTransferringInstallations());
            transferredCO2Approach.setDeductionsToAmountOfTransferredCO2(section.getDeductionsToAmountOfTransferredCO2());
            transferredCO2Approach.setProcedureForLeakageEvents(section.getProcedureForLeakageEvents());
            transferredCO2Approach.setTemperaturePressure(section.getTemperaturePressure());
            transferredCO2Approach.setTransferOfCO2(section.getTransferOfCO2());
            transferredCO2Approach.setQuantificationMethodologies(section.getQuantificationMethodologies());
            transferredCO2Approach.setApproachDescription(section.getApproachDescription());
        });
    }

    @Override
    public Map<String, TransferredCO2MonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        StringBuilder queryInstallationBuilder = new StringBuilder(INSTALLATION_QUERY);
        StringBuilder queryTemperatureBuilder = new StringBuilder(TEMPERATURE_QUERY);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where a.fldEmitterID in (%s)", inAccountIdsSql));
            queryInstallationBuilder.append(String.format("where a.fldEmitterID in (%s)", inAccountIdsSql));
            queryTemperatureBuilder.append(String.format("where a.fldEmitterID in (%s)", inAccountIdsSql));
        }

        Map<String, EtsTransferredCO2MonitoringApproach> etsTransferredCO2MonitoringApproachesMap =
                executeQuery(queryBuilder.toString(), accountIds);
        List<EtsReceivingTransferringInstallation> etsReceivingTransferringInstallations =
                executeInstallationQuery(queryInstallationBuilder.toString(), accountIds);
        List<EtsTemperaturePressure> etsTemperaturePressures =
                executeTemperatureQuery(queryTemperatureBuilder.toString(), accountIds);

        return TransferredCO2ApproachDetailsMapper.transformToTransferredCO2MonitoringApproach(etsTransferredCO2MonitoringApproachesMap,
                etsReceivingTransferringInstallations, etsTemperaturePressures);
    }

    private Map<String, EtsTransferredCO2MonitoringApproach> executeQuery(String query, List<String> accountIds) {
        List<EtsTransferredCO2MonitoringApproach> etsTransferredCO2MonitoringApproaches = migrationJdbcTemplate.query(query,
                new EtsTransferredCO2MonitoringApproachRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());

        return etsTransferredCO2MonitoringApproaches.stream()
                .collect(Collectors.toMap(EtsTransferredCO2MonitoringApproach::getEtsAccountId,
                        etsN2OMonitoringApproachDetails -> etsN2OMonitoringApproachDetails));
    }

    private List<EtsReceivingTransferringInstallation> executeInstallationQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsReceivingTransferringInstallationRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }

    private List<EtsTemperaturePressure> executeTemperatureQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsTemperaturePressureRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }
}
