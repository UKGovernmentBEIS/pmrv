package uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.details;

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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCTier2EmissionFactor;

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
public class PFCMonitoringApproachesDetailsMigrationService implements PermitSectionMigrationService<PFCMonitoringApproach> {

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
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_monitoring_of_pfc_description)[1]', 'NVARCHAR(max)'),'')) Mpfc_monitoring_of_pfc_description,\r\n" +
                    "-- Collection Efficiency for Fugitive Emissions\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_fugitive_emissions-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_fugitive_emissions_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Tier 2 Emission Factor\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_tier_2_ef_applied)[1]', 'NVARCHAR(max)'),'')) Mpfc_tier_2_ef_applied,\r\n" +
                    "-- Determination of Installation Specific Emission Factors - If Mpfc_tier_2_ef_applied = 'Yes'\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_determination_of_emission_factors-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_determination_of_emission_factors_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Schedule of Measurements - If Mpfc_tier_2_ef_applied = 'Yes'\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Mpfc_proc_schedule_of_measurements-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Mpfc_proc_schedule_of_measurements_Procedure_cen_or_other_standards_applied\r\n" +
                    "            --, fldSubmittedXML\r\n" +
                    " from tblEmitter E\r\n" +
                    "     join latestPermit F on E.fldEmitterID = F.fldEmitterID\r\n" +
                    "where fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_pfc)[1]', 'NVARCHAR(max)') = 'Yes'"
            ;

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, PFCMonitoringApproach> sections = queryEtsSection(accountIds);

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            PFCMonitoringApproach pfcMonitoringApproach =
                    (PFCMonitoringApproach)permitMigrationContainer
                    .getPermitContainer()
                    .getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(MonitoringApproachType.PFC);
            pfcMonitoringApproach.setApproachDescription(section.getApproachDescription());
            pfcMonitoringApproach.setCollectionEfficiency(section.getCollectionEfficiency());
            pfcMonitoringApproach.setTier2EmissionFactor(section.getTier2EmissionFactor());
        });
    }

    @Override
    public Map<String, PFCMonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("and e.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();

        Map<String, EtsPFCMonitoringApproachDetails> etsPFCMonitoringApproachDetailsMap = executeQuery(query, accountIds);

        Map<String, PFCMonitoringApproach> pfcMonitoringApproaches = new HashMap<>();
        etsPFCMonitoringApproachDetailsMap.forEach((etsAccountId, etsPFCMonitoringApproachDetails) -> {
            String approachDescription = PFCApproachDetailsMapper.constructPFCApproachDescription(etsPFCMonitoringApproachDetails);
            ProcedureForm collectionEfficiency = PFCApproachDetailsMapper.constructCollectionEfficiencyProcedure(etsPFCMonitoringApproachDetails);
            PFCTier2EmissionFactor tier2EmissionFactor = PFCApproachDetailsMapper.constructTier2EmissionFactor(etsPFCMonitoringApproachDetails);


            pfcMonitoringApproaches.put(etsAccountId,
                    PFCMonitoringApproach.builder()
                            .approachDescription(approachDescription)
                            .collectionEfficiency(collectionEfficiency)
                            .tier2EmissionFactor(tier2EmissionFactor)
                            .build());
        });
        return pfcMonitoringApproaches;
    }

    private Map<String, EtsPFCMonitoringApproachDetails> executeQuery(String query, List<String> accountIds) {
        List<EtsPFCMonitoringApproachDetails> etsPFCMonitoringApproachesDetails = migrationJdbcTemplate.query(query,
                new EtsPFCMonitoringApproachDetailsRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsPFCMonitoringApproachesDetails
                .stream()
                .collect(Collectors.toMap(EtsPFCMonitoringApproachDetails::getEtsAccountId,
                        etsPFCMonitoringApproachDetails -> etsPFCMonitoringApproachDetails));
    }
}
