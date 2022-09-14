package uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.cellanodetypes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.CellAndAnodeType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCMonitoringApproach;

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
public class PFCMonitoringApproachesCellAndAnodeTypesMigrationService implements PermitSectionMigrationService<PFCMonitoringApproach> {

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
                    "trim(nullif(T.c.query('Mpfc_cell_type ').value('.', 'NVARCHAR(MAX)'),'')) Mpfc_cell_type ,\r\n" +
                    "trim(nullif(T.c.query('Mpfc_anode_type').value('.', 'NVARCHAR(MAX)'),'')) Mpfc_anode_type\r\n" +
                    "            --, fldSubmittedXML\r\n" +
                    "  from tblEmitter E\r\n" +
                    "     join latestPermit F on E.fldEmitterID = F.fldEmitterID\r\n" +
                    "cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Mpfc_cell_and_anode_types/row)') as T(c)\r\n" +
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
            pfcMonitoringApproach.setCellAndAnodeTypes(section.getCellAndAnodeTypes());
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

        Map<String, List<EtsCellAndAnodeType>> etsPFCMonitoringApproachCellAndAnodeTypesMap = executeQuery(query, accountIds);

        Map<String, PFCMonitoringApproach> pfcMonitoringApproaches = new HashMap<>();
        etsPFCMonitoringApproachCellAndAnodeTypesMap.forEach((etsAccountId, etsPFCMonitoringApproachCellAndAnodeTypes) -> {
            List<CellAndAnodeType> cellAndAnodeTypes = PFCApproachCellAndAnodeTypesMapper.constructPFCCellAndAnodeTypes(etsPFCMonitoringApproachCellAndAnodeTypes);

            pfcMonitoringApproaches.put(etsAccountId,
                    PFCMonitoringApproach.builder()
                            .cellAndAnodeTypes(cellAndAnodeTypes)
                            .build());
        });
        return pfcMonitoringApproaches;
    }

    private Map<String, List<EtsCellAndAnodeType>> executeQuery(String query, List<String> accountIds) {
        List<EtsCellAndAnodeType> etsPFCMonitoringApproachesCellAndAnodeTypes = migrationJdbcTemplate.query(query,
                new CellAndAnodeTypeRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsPFCMonitoringApproachesCellAndAnodeTypes
                .stream()
                .collect(Collectors.groupingBy(EtsCellAndAnodeType::getEtsAccountId));
    }
}
