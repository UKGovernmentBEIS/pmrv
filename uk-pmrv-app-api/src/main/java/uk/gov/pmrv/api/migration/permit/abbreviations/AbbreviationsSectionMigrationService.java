package uk.gov.pmrv.api.migration.permit.abbreviations;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class AbbreviationsSectionMigrationService implements PermitSectionMigrationService<Abbreviations> {

    private final JdbcTemplate migrationJdbcTemplate;
    private final MigrationAbbreviationsMapper migrationAbbreviationsMapper;

    private static final String QUERY_BASE  =
        "with \r\n" +
            "   XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd), \r\n" +
            "   allPermits as ( \r\n" +
            "       select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID \r\n" +
            "       from tblForm F \r\n" +
            "       join tblFormData FD on FD.fldFormID = F.fldFormID \r\n" +
            "       join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID \r\n" +
            "       join tlkpFormType FT on FTP.fldFormTypeID = FT.fldFormTypeID \r\n" +
            "       join tlkpPhase P on FTP.fldPhaseID = P.fldPhaseID \r\n" +
            "       where FD.fldMinorVersion = 0 \r\n" +
            "       and P.fldDisplayName = 'Phase 3' \r\n" +
            "       and FT.fldName = 'IN_PermitApplication'), \r\n" +
            "   mxPVer as ( \r\n" +
            "       select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion' \r\n" +
            "       from allPermits \r\n" +
            "       group by fldFormID), \r\n" +
            "   latestPermit as ( \r\n" +
            "       select p.fldEmitterID, FD.* \r\n" +
            "       from allPermits p \r\n" +
            "       join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion \r\n" +
            "       join tblFormData FD on FD.fldFormDataID = p.fldFormDataID) \r\n" +
            "select \r\n" +
            "E.fldEmitterID as emitterId, \r\n" +
            "T.c.query('Ab_abbreviation').value('.', 'NVARCHAR(MAX)') AS abbreviation, \r\n" +
            "T.c.query('Ab_definition').value('.', 'NVARCHAR(MAX)') AS definition \r\n" +
            "from tblEmitter E \r\n" +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" +
            "join latestPermit F       on E.fldEmitterID = F.fldEmitterID \r\n" +
            "OUTER APPLY f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ab_abbreviations/row') T(c) \r\n";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, Abbreviations> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setAbbreviations(section));        
    }

    @Override
    public Map<String, Abbreviations> queryEtsSection(List<String> accountIds) {
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);

        Map<String, List<EtsAbbreviation>> etsAbbreviations = executeQuery(query, accountIds);

        return etsAbbreviations.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> toPmrvAbbreviations(entry.getValue())));
    }

    private Map<String, List<EtsAbbreviation>> executeQuery(String query, List<String> accountIds) {
        List<EtsAbbreviation> etsAbbreviations = migrationJdbcTemplate.query(
                query,
                new EtsAbbreviationRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsAbbreviations
            .stream()
            .collect(Collectors.groupingBy(EtsAbbreviation::getEtsAccountId));
    }

    private Abbreviations toPmrvAbbreviations(List<EtsAbbreviation> etsAbbreviations) {
        Abbreviations abbreviations = Abbreviations.builder()
            .exist(false)
            .build();

        if(existAbbreviations(etsAbbreviations)) {
            abbreviations.setExist(true);
            abbreviations.setAbbreviationDefinitions(migrationAbbreviationsMapper.toAbbreviationDefinitions(etsAbbreviations));
        };

        return abbreviations;
    }

    private boolean existAbbreviations(List<EtsAbbreviation> etsAbbreviations) {
        if (etsAbbreviations.isEmpty()) {
            return false;
        }

        if(etsAbbreviations.size() == 1 ) {
            EtsAbbreviation etsAbbreviation = etsAbbreviations.get(0);
            if(etsAbbreviation.getAbbreviation() == null && etsAbbreviation.getDefinition() == null) {
                return false;
            }
        }

        return true;
    }

}
