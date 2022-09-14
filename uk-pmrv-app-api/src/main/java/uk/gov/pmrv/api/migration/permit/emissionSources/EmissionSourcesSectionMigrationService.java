package uk.gov.pmrv.api.migration.permit.emissionSources;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmissionSourcesSectionMigrationService implements PermitSectionMigrationService<EmissionSources> {

    private final JdbcTemplate migrationJdbcTemplate;
    private static final MigrationEmissionSourceMapper migrationEmissionSourceMapper =
        Mappers.getMapper(MigrationEmissionSourceMapper.class);

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
            "T.c.query('Ed_emission_source_reference').value('.', 'NVARCHAR(MAX)') AS reference, \r\n" +
            "T.c.query('Ed_emission_source_description').value('.', 'NVARCHAR(MAX)') AS description \r\n" +
            "from tblEmitter E \r\n" +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" +
            "join latestPermit F       on E.fldEmitterID = F.fldEmitterID \r\n" +
            "OUTER APPLY f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ed_emission_sources/row') T(c) \r\n"
        ;
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, EmissionSources> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setEmissionSources(section));        
    }

    @Override
    public Map<String, EmissionSources> queryEtsSection(List<String> accountIds) {
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);

        Map<String, EmissionSources> accountSections = new HashMap<>();

        Map<String, List<EtsEmissionSource>> accountEmissionSources = executeQuery(query, accountIds);

        for(Map.Entry<String, List<EtsEmissionSource>> entry :accountEmissionSources.entrySet()) {
            EmissionSources emissionSourcesSection = EmissionSources.builder()
                .emissionSources(migrationEmissionSourceMapper.toEmissionSources(entry.getValue()))
                .build();
            accountSections.put(entry.getKey(), emissionSourcesSection);
        }

        return accountSections;
    }

    private Map<String, List<EtsEmissionSource>> executeQuery(String query, List<String> accountIds) {
        List<EtsEmissionSource> accountEmissionSources = migrationJdbcTemplate.query(query,
                new EtsEmissionSourceRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return accountEmissionSources
            .stream()
            .collect(Collectors.groupingBy(EtsEmissionSource::getEtsAccountId));
    }

}
