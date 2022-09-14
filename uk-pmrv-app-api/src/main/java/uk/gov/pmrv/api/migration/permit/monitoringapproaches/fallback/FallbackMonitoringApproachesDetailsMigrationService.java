package uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback;

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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;

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
public class FallbackMonitoringApproachesDetailsMigrationService implements PermitSectionMigrationService<FallbackMonitoringApproach> {

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
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_fallback_approach_description)[1]', 'NVARCHAR(max)'),'')) Fb_fallback_approach_description,\r\n" +
                    "-- Approach Justification\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_fallback_approach_justification)[1]', 'NVARCHAR(max)'),'')) Fb_fallback_approach_justification,\r\n" +
                    "-- Annual Uncertainty Analysis\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_title                         ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_reference                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_diagram_reference             ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_description                   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_responsible_post_department   ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_location                      ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_it_system                     ,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Fb_annual_uncertainty_analysis_procedure-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Fb_annual_uncertainty_analysis_procedure_Procedure_cen_or_other_standards_applied\r\n" +
                    "            --, fldSubmittedXML\r\n" +
                    " from tblEmitter E\r\n" +
                    "     join latestPermit F on E.fldEmitterID = F.fldEmitterID\r\n" +
                    "where fldSubmittedXML.value('(fd:formdata/fielddata/Ma_fallback_approach)[1]', 'NVARCHAR(max)') = 'Yes'\r\n"
            ;

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, FallbackMonitoringApproach> sections = queryEtsSection(accountIds);

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            FallbackMonitoringApproach fallbackMonitoringApproach =
                    (FallbackMonitoringApproach)permitMigrationContainer
                    .getPermitContainer()
                    .getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(MonitoringApproachType.FALLBACK);
            fallbackMonitoringApproach.setApproachDescription(section.getApproachDescription());
            fallbackMonitoringApproach.setJustification(section.getJustification());
            fallbackMonitoringApproach.setAnnualUncertaintyAnalysis(section.getAnnualUncertaintyAnalysis());
        });
    }

    @Override
    public Map<String, FallbackMonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("and e.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();

        Map<String, EtsFallbackMonitoringApproachDetails> etsFallbackMonitoringApproachDetailsMap = executeQuery(query, accountIds);

        Map<String, FallbackMonitoringApproach> fallbackMonitoringApproaches = new HashMap<>();
        etsFallbackMonitoringApproachDetailsMap.forEach((etsAccountId, etsFallbackMonitoringApproachDetails) -> {
            String approachDescription = FallbackApproachDetailsMapper.constructFallbackApproachDescription(etsFallbackMonitoringApproachDetails);
            String approachJustification = FallbackApproachDetailsMapper.constructFallbackApproachJustification(etsFallbackMonitoringApproachDetails);
            ProcedureForm annualUncertaintyAnalysis = FallbackApproachDetailsMapper.constructAnnualUncertaintyAnalysis(etsFallbackMonitoringApproachDetails);

            fallbackMonitoringApproaches.put(etsAccountId,
                    FallbackMonitoringApproach.builder()
                            .approachDescription(approachDescription)
                            .justification(approachJustification)
                            .annualUncertaintyAnalysis(annualUncertaintyAnalysis)
                            .build());
        });
        return fallbackMonitoringApproaches;
    }

    private Map<String, EtsFallbackMonitoringApproachDetails> executeQuery(String query, List<String> accountIds) {
        List<EtsFallbackMonitoringApproachDetails> etsFallbackMonitoringApproachesDetails = migrationJdbcTemplate.query(query,
                new EtsFallbackMonitoringApproachDetailsRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsFallbackMonitoringApproachesDetails
                .stream()
                .collect(Collectors.toMap(EtsFallbackMonitoringApproachDetails::getEtsAccountId,
                        etsFallbackMonitoringApproachDetails -> etsFallbackMonitoringApproachDetails));
    }
}
