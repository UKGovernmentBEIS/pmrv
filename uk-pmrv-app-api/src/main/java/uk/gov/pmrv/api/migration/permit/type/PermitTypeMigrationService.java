package uk.gov.pmrv.api.migration.permit.type;

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
import uk.gov.pmrv.api.permit.domain.PermitType;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class PermitTypeMigrationService {

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
        "when Source_workflow_type in ('INPermitApplication', 'INPermitApplicationRePermitting', 'NULL') then Pd_installation_excluded " +
        "when Source_workflow_type in ('INVariation') then Vd_installation_excluded " +
        "when Source_workflow_type in ('INTransferPartB', 'INTransferAmalgamation') then Et_installation_excluded " +
        "end is_installation_excluded " +
        "from t1 " +
        ") " +
        "select fldEmitterID, case is_installation_excluded when 'Yes' then 'HSE' when 'No' then 'GHGE' end permitType from t2 ";

    public void populateType(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        Map<String, PermitType> permitsWithTypes =
            queryEts(new ArrayList<>(accountsToMigratePermit.keySet()));

        permitsWithTypes
            .forEach((etsAccId, type) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId()).getPermitContainer().setPermitType(type));
    }

    public Map<String, PermitType> queryEts(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();
        List<EtsPermitType> etsPermitTypes = executeQuery(query, accountIds);

        return etsPermitTypes.stream()
            .collect(Collectors.toMap(EtsPermitType::getEtsAccountId,
                etsPermitType -> PermitType.valueOf(etsPermitType.getType())));
    }

    private List<EtsPermitType> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
            new EtsPermitTypeRowMapper(),
            accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }
}
