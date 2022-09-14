package uk.gov.pmrv.api.migration.permit.confidentialitystatement;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class ConfidentialityStatementMigrationService implements PermitSectionMigrationService<ConfidentialityStatement> {

    private final JdbcTemplate migrationJdbcTemplate;
    private final MigrationConfidentialityStatementMapper migrationConfidentialityStatementMapper;

    private static final String QUERY_BASE =
        "with XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd), \r\n" +
        "allPermits as ( \r\n" +
        "select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID \r\n" +
        "from tblForm F \r\n" +
        "join tblFormData FD on FD.fldFormID = F.fldFormID \r\n" +
        "join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID \r\n" +
        "join tlkpFormType FT on FTP.fldFormTypeID = FT.fldFormTypeID \r\n" +
        "join tlkpPhase P on FTP.fldPhaseID = P.fldPhaseID \r\n" +
        "where FD.fldMinorVersion = 0 \r\n" +
        "and P.fldDisplayName = 'Phase 3' \r\n" +
        "and FT.fldName = 'IN_PermitApplication'), \r\n" +
        "mxPVer as ( \r\n" +
        "select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion' \r\n" +
        "from allPermits \r\n" +
        "group by fldFormID), \r\n" +
        "latestPermit as ( \r\n" +
        "select p.fldEmitterID, FD.* \r\n" +
        "from allPermits p \r\n" +
        "join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion \r\n" +
        "join tblFormData FD on FD.fldFormDataID = p.fldFormDataID \r\n" +
        "join tblEmitter E on E.fldEmitterID = p.fldEmitterID \r\n" +
        "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live') \r\n" +
        "select \r\n" +
        "e.fldEmitterID as emitterId, \r\n" +
        "fldSubmittedXML.value('(fd:formdata/fielddata/Con_confidentiality-Commercially_confidential)[1]', 'NVARCHAR(max)') as existConfidentialityStatement, \r\n" +
        "T.c.query('Section_confidentiality').value('.', 'NVARCHAR(MAX)') as section, \r\n" +
        "T.c.query('Justification').value('.', 'NVARCHAR(MAX)') as justification \r\n" +
        "from latestPermit e \r\n" +
        "OUTER APPLY fldSubmittedXML.nodes('fd:formdata/fielddata/Con_confidentiality-Confidential_sections/row') T(c) \r\n";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, ConfidentialityStatement> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setConfidentialityStatement(section));
    }

    @Override
    public Map<String, ConfidentialityStatement> queryEtsSection(List<String> accountIds) {
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);

        Map<String, List<EtsConfidentialityStatement>> etsConfidentialityStatements = executeQuery(query, accountIds);

        Map<String, ConfidentialityStatement> accountSections = new HashMap<>();

        for(Map.Entry<String, List<EtsConfidentialityStatement>> entry :etsConfidentialityStatements.entrySet()) {
            accountSections.put(entry.getKey(), toConfidentialityStatement(entry.getValue()));
        }

        return accountSections;
    }

    private Map<String, List<EtsConfidentialityStatement>> executeQuery(String query, List<String> accountIds) {
        List<EtsConfidentialityStatement> etsConfidentialityStatements = migrationJdbcTemplate.query(query,
                new EtsConfidentialityStatementRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsConfidentialityStatements
            .stream()
            .collect(Collectors.groupingBy(EtsConfidentialityStatement::getEtsAccountId));
    }

    private ConfidentialityStatement toConfidentialityStatement(List<EtsConfidentialityStatement> etsConfidentialityStatements) {

        return etsConfidentialityStatements.isEmpty() || !etsConfidentialityStatements.get(0).isExist() ?
            ConfidentialityStatement.builder()
                .exist(false)
                .build() :
            ConfidentialityStatement.builder()
                .exist(true)
                .confidentialSections(
                    migrationConfidentialityStatementMapper.toConfidentialSections(etsConfidentialityStatements))
                .build();
    }

}
