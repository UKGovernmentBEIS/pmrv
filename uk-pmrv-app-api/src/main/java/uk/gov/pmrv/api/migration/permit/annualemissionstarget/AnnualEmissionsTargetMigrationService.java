package uk.gov.pmrv.api.migration.permit.annualemissionstarget;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class AnnualEmissionsTargetMigrationService {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE = "with XMLNAMESPACES (" +
        "'urn:www-toplev-com:officeformsofd' AS fd" +
        "), allPermits as (" +
        "select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldMajorVersion versionKey " +
        "from tblForm F " +
        "join tblFormData FD        on FD.fldFormID = F.fldFormID " +
        "join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID " +
        "join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID " +
        "join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID " +
        "where P.fldDisplayName = 'Phase 3' " +
        "and FT.fldName = 'IN_PermitApplication' " +
        "), latestVersion as ( " +
        "select fldEmitterID, max(versionKey) MaxVersionKey " +
        "from allPermits " +
        "group by fldEmitterID " +
        "), latestPermit as ( " +
        "select p.fldEmitterID, FD.* " +
        "from allPermits p " +
        "join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.versionKey = v.MaxVersionKey and p.fldMinorVersion = 0 " +
        "join tblFormData FD on FD.fldFormDataID = p.fldFormDataID " +
        "join tblEmitter E   on E.fldEmitterID = p.fldEmitterID " +
        "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' " +
        "), t1 as ( " +
        "select fldEmitterID, " +
        "fldSubmittedXML.value('(fd:formdata/fielddata/Pd_installation_excluded)[1]', 'NVARCHAR(max)') Pd_installation_excluded, " +
        "fldSubmittedXML.value('(fd:formdata/fielddata/Vd_installation_excluded)[1]', 'NVARCHAR(max)') Vd_installation_excluded, " +
        "fldSubmittedXML.value('(fd:formdata/fielddata/Et_installation_excluded)[1]', 'NVARCHAR(max)') Et_installation_excluded, " +
        "isnull(fldSubmittedXML.value('(fd:formdata/fielddata/Source_workflow_type)[1]', 'NVARCHAR(max)'), 'NULL') Source_workflow_type " +
        "from latestPermit f " +
        "), t2 as ( " +
        "select fldEmitterID, " +
        "case " +
        "when Source_workflow_type in ('INPermitApplication', 'INPermitApplicationRePermitting', 'NULL') and Pd_installation_excluded = 'Yes' then 1 " +
        "when Source_workflow_type in ('INVariation') and Vd_installation_excluded = 'Yes' then 2 " +
        "when Source_workflow_type in ('INTransferPartB', 'INTransferAmalgamation') and Et_installation_excluded = 'Yes' then 3 " +
        "else 0 " +
        "end installation_excluded " +
        "from t1 " +
        "), p1 as ( " +
        "select f.fldEmitterID, " +
        "nullif(trim(T.c.query('Pd_monitoring_year').value('.', 'NVARCHAR(MAX)')), '') monitoring_year, " +
        "nullif(trim(T.c.query('Pd_emissions_target').value('.', 'NVARCHAR(MAX)')), '') emissions_target, " +
        "t2.installation_excluded " +
        "from latestPermit f " +
        "join t2 on t2.fldEmitterID = f.fldEmitterID " +
        "outer APPLY f.fldSubmittedXML.nodes('(fd:formdata/fielddata/Pd_annual_emissions_target/row)') as T(c) " +
        "where t2.installation_excluded = 1 " +
        "union all " +
        "select f.fldEmitterID, " +
        "nullif(trim(T.c.query('Vd_monitoring_year').value('.', 'NVARCHAR(MAX)')), '') monitoring_year, " +
        "nullif(trim(T.c.query('Vd_emissions_target').value('.', 'NVARCHAR(MAX)')), '') emissions_target, " +
        "t2.installation_excluded " +
        "from latestPermit f " +
        "join t2 on t2.fldEmitterID = f.fldEmitterID " +
        "outer APPLY f.fldSubmittedXML.nodes('(fd:formdata/fielddata/Vd_annual_emissions_target/row)') as T(c) " +
        "where t2.installation_excluded = 2 " +
        "union all " +
        "select f.fldEmitterID, " +
        "nullif(trim(T.c.query('Et_monitoring_year').value('.', 'NVARCHAR(MAX)')), '') monitoring_year, " +
        "nullif(trim(T.c.query('Et_emissions_target').value('.', 'NVARCHAR(MAX)')), '') emissions_target, " +
        "t2.installation_excluded " +
        "from latestPermit f " +
        "join t2 on t2.fldEmitterID = f.fldEmitterID " +
        "outer APPLY f.fldSubmittedXML.nodes('(fd:formdata/fielddata/Et_annual_emissions_target/row)') as T(c) " +
        "where t2.installation_excluded = 3 " +
        ") " +
        "select fldEmitterID, monitoring_year, emissions_target from p1 ";

    public void populateAnnualEmissionsTarget(Map<String, Account> accountsToMigratePermit,
                                              Map<Long, PermitMigrationContainer> permits) {
        Map<String, SortedMap<String, BigDecimal>> permitsWithAnnualEmissionsTarget =
            queryEts(new ArrayList<>(accountsToMigratePermit.keySet()));

        permitsWithAnnualEmissionsTarget
            .forEach((etsAccId, aet) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId()).getPermitContainer().setAnnualEmissionsTargets(aet));
    }

    public Map<String, SortedMap<String, BigDecimal>> queryEts(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();
        Map<String, List<EtsAnnualEmissionsTarget>> etsAnnualEmissionsTargetsByAccount = executeQuery(query, accountIds);

        return etsAnnualEmissionsTargetsByAccount.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> toAnnualEmissionsTargetMap(e.getValue()))
            );
    }

    private Map<String, List<EtsAnnualEmissionsTarget>> executeQuery(String query, List<String> accountIds) {
        List<EtsAnnualEmissionsTarget> etsAnnualEmissionsTargets = migrationJdbcTemplate.query(query,
            new EtsAnnualEmissionsTargetRowMapper(),
            accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsAnnualEmissionsTargets
            .stream()
            .collect(Collectors.groupingBy(EtsAnnualEmissionsTarget::getEtsAccountId));
    }

    private SortedMap<String, BigDecimal> toAnnualEmissionsTargetMap(List<EtsAnnualEmissionsTarget> etsAnnualEmissionsTargets) {
        return etsAnnualEmissionsTargets.stream()
            .peek(aet -> log.debug("Collecting emission targets for account id: {} and year: {}", aet.getEtsAccountId(), aet.getMonitoringYear()))
            .collect(Collectors.toMap(
                EtsAnnualEmissionsTarget::getMonitoringYear,
                aet -> {
                    if(NumberUtils.isParsable(aet.getEmissionsTarget())) {
                        return new BigDecimal(aet.getEmissionsTarget());
                    }
                    //TODO: check what should happen if alue in ETSWAP is not parsable. Here,an invalid value is returned in order to trigger permit validator
                    return BigDecimal.valueOf(999999.999);

                },
                (oldVal, newVal) -> {
                    log.error("More than one emissions target has been found for this monitoring year");
                    return newVal;
                },
                TreeMap::new
                )
            );
    }
}
