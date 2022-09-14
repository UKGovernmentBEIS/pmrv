package uk.gov.pmrv.api.migration.permit.monitoringapproaches.inherentco2;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class InherentCO2MonitoringApproachDescriptionMigrationService implements PermitSectionMigrationService<InherentCO2MonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE =
            "with XMLNAMESPACES ( \n" +
                    "'urn:www-toplev-com:officeformsofd' AS fd \n" +
                    " ), allPermits as ( \n" +
                    "select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, \n" +
                    "  FD.fldMajorVersion versionKey \n" +
                    " from tblForm F \n" +
                    " join tblFormData FD        on FD.fldFormID = F.fldFormID \n" +
                    " join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID \n" +
                    " join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID \n" +
                    " join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID \n" +
                    "where P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication' \n" +
                    " ), latestVersion as ( \n" +
                    "  select fldEmitterID, max(versionKey) MaxVersionKey from allPermits group by fldEmitterID \n" +
                    "), latestPermit as ( \n" +
                    " select p.fldEmitterID, FD.* \n" +
                    " from allPermits p \n" +
                    " join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.fldMajorVersion = v.MaxVersionKey and p.fldMinorVersion = 0  \n" +
                    " join tblFormData FD on FD.fldFormDataID = p.fldFormDataID \n" +
                    " join tblEmitter E   on E.fldEmitterID = p.fldEmitterID \n" +
                    " join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \n" +
                    "), a as ( \n" +
                    " select fldEmitterID, \n" +
                    " trim(fldSubmittedXML.value('(fd:formdata/fielddata/Mtico2_monitoring_of_transferred_inherent_co2_description)[1]', 'NVARCHAR(max)')) Mtico2_monitoring_of_transferred_inherent_co2_description  \n" +
                    " from latestPermit  \n" +
                    " where fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_transferred_inherent_co2)[1]', 'NVARCHAR(max)') = 'Yes' \n" +
                    ") \n" +
                    " select * from a ";

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, InherentCO2MonitoringApproach> sections = queryEtsSection(accountIds);

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            InherentCO2MonitoringApproach inherentApproach =
                    (InherentCO2MonitoringApproach) permitMigrationContainer
                            .getPermitContainer()
                            .getPermit()
                            .getMonitoringApproaches()
                            .getMonitoringApproaches()
                            .get(MonitoringApproachType.INHERENT_CO2);

            inherentApproach.setApproachDescription(section.getApproachDescription());
        });
    }

    @Override
    public Map<String, InherentCO2MonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where a.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();
        Map<String, EtsInherentCO2MonitoringApproachDescription> approachDescriptionMap = executeQuery(query, accountIds);

        return approachDescriptionMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> InherentCO2MonitoringApproach.builder()
                                .approachDescription(entry.getValue().getApproachDescription()).build()));
    }

    private Map<String, EtsInherentCO2MonitoringApproachDescription> executeQuery(String query, List<String> accountIds) {
        List<EtsInherentCO2MonitoringApproachDescription> etsInherentCO2MonitoringApproachDescriptions = migrationJdbcTemplate.query(query,
                new EtsInherentCO2MonitoringApproachDescriptionRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());

        return etsInherentCO2MonitoringApproachDescriptions
                .stream()
                .collect(Collectors.toMap(EtsInherentCO2MonitoringApproachDescription::getEtsAccountId,
                        etsPFCMonitoringApproachDetails -> etsPFCMonitoringApproachDetails));
    }
}
