package uk.gov.pmrv.api.migration.permit.activationdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class ActivationDateMigrationService {

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
        "isnull( " +
        "fldSubmittedXML.value('(fd:formdata/fielddata/Pd_grant_permit_effective_date)[1]', 'NVARCHAR(max)'), " +
        "fldSubmittedXML.value('(fd:formdata/fielddata/Vd_grant_variation_effective_date)[1]', 'NVARCHAR(max)') " +
        ") grant_effective_date " +
        "from latestPermit f " +
        ") " +
        "select fldEmitterID, convert(date, grant_effective_date) grantEffectiveDate from t1 ";

    public void populateActivationDate(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        Map<String, LocalDate> permitsWithActivationDate =
            queryEts(new ArrayList<>(accountsToMigratePermit.keySet()));

        permitsWithActivationDate
            .forEach((etsAccId, date) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId()).getPermitContainer().setActivationDate(date));
    }

    public Map<String, LocalDate> queryEts(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();
        List<EtsActivationDate> etsActivationDates = executeQuery(query, accountIds);

        return etsActivationDates.stream()
            .collect(Collectors.toMap(EtsActivationDate::getEtsAccountId, EtsActivationDate::getActivationDate));
    }

    private List<EtsActivationDate> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
            new EtsActivationDateRowMapper(),
            accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }
}
