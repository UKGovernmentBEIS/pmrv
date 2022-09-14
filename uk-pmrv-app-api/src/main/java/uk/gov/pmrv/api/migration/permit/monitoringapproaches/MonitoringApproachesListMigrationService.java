package uk.gov.pmrv.api.migration.permit.monitoringapproaches;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.MigrationPermitHelper;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MonitoringApproachesListMigrationService implements PermitSectionMigrationService<MonitoringApproaches> {

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
                    ") \r\n" +
                    "select E.fldEmitterID,\r\n" +
                    "   fldSubmittedXML.value('(fd:formdata/fielddata/Ma_calculation)[1]', 'NVARCHAR(max)') Ma_calculation,\r\n" +
                    "   fldSubmittedXML.value('(fd:formdata/fielddata/Ma_measurement)[1]', 'NVARCHAR(max)') Ma_measurement,\r\n" +
                    "   fldSubmittedXML.value('(fd:formdata/fielddata/Ma_fallback_approach)[1]', 'NVARCHAR(max)') Ma_fallback_approach,\r\n" +
                    "   fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_n2o)[1]', 'NVARCHAR(max)') Ma_monitoring_of_n2o,\r\n" +
                    "   fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_pfc)[1]', 'NVARCHAR(max)') Ma_monitoring_of_pfc,\r\n" +
                    "   fldSubmittedXML.value('(fd:formdata/fielddata/Ma_monitoring_of_transferred_inherent_co2)[1]', 'NVARCHAR(max)') Ma_monitoring_of_transferred_inherent_co2\r\n" +
                    "from tblEmitter E \r\n" +
                    "join latestPermit F on E.fldEmitterID = F.fldEmitterID\r\n"
            ;

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        Map<String, MonitoringApproaches> sections =
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));

        sections.forEach((etsAccId, section) ->
                        permits.get(accountsToMigratePermit.get(etsAccId).getId())
                                .getPermitContainer().getPermit().setMonitoringApproaches(section));
    }

    @Override
    public Map<String, MonitoringApproaches> queryEtsSection(List<String> accountIds) {
        String query = MigrationPermitHelper.constructEtsSectionQuery(QUERY_BASE, accountIds);

        Map<String, EtsMonitoringApproachesList> etsAccountMonitoringApproaches = executeQuery(query, accountIds);
        Map<String, MonitoringApproaches> accountMonitoringApproaches = new HashMap<>();
        etsAccountMonitoringApproaches.forEach((etsAccountId, monitoringApproaches) ->
                accountMonitoringApproaches.put(etsAccountId,
                        MonitoringApproachesListMapper.constructMonitoringApproaches(monitoringApproaches)));
        return accountMonitoringApproaches;
    }

    private Map<String, EtsMonitoringApproachesList> executeQuery(String query, List<String> accountIds) {
        List<EtsMonitoringApproachesList> accountMonitoringApproaches = migrationJdbcTemplate.query(query,
                new EtsMonitoringApproachesListRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return accountMonitoringApproaches
                .stream()
                .collect(Collectors.toMap(EtsMonitoringApproachesList::getEtsAccountId, etsMonitoringApproach -> etsMonitoringApproach));
    }

}
