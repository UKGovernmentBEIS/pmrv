package uk.gov.pmrv.api.migration.permit.emissionpoints;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmissionPointsSectionMigrationService implements PermitSectionMigrationService<EmissionPoints> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE = "with XMLNAMESPACES (" +
            "'urn:www-toplev-com:officeformsofd' AS fd" +
            "), allPermits as (" +
            "select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID " +
            "from tblForm F " +
            "join tblFormData FD        on FD.fldFormID = F.fldFormID " +
            "join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID " +
            "join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID " +
            "join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID " +
            "where FD.fldMinorVersion = 0 " +
            "and P.fldDisplayName = 'Phase 3' " +
            "and FT.fldName = 'IN_PermitApplication' " +
            "), mxPVer as (" +
            "select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion' " +
            "from allPermits " +
            "group by fldFormID " +
            "), latestPermit as (" +
            "select p.fldEmitterID, FD.* " +
            "from allPermits p " +
            "join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion " +
            "join tblFormData FD on FD.fldFormDataID = p.fldFormDataID " +
            ") " +
            "select " +
            "E.fldEmitterID as emitterId, " +
            "T.c.query('Ed_emission_point_reference').value('.', 'NVARCHAR(MAX)') AS reference, " +
            "T.c.query('Ed_emission_point_description').value('.', 'NVARCHAR(MAX)') AS description " +
            "from tblEmitter E " +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' " +
            "join latestPermit F on E.fldEmitterID = F.fldEmitterID " +
            "OUTER APPLY f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ed_emission_point/row') T(c)";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, EmissionPoints> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setEmissionPoints(section));        
    }

    @Override
    public Map<String, EmissionPoints> queryEtsSection(List<String> accountIds) {
        // Create sql statement
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);

        Map<String, List<EtsEmissionPoint>> results = migrationJdbcTemplate.query(query,
                new EmissionPointMigrationServiceRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray()).stream().collect(Collectors.groupingBy(EtsEmissionPoint::getEmitterId));

        return results.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> transform(e.getValue())));
    }

    private EmissionPoints transform(List<EtsEmissionPoint> etsResults) {
        List<EmissionPoint> points = new ArrayList<>();

        IntStream.range(0, etsResults.size()).forEach(num ->
                points.add(EmissionPoint.builder()
                        .id(UUID.randomUUID().toString()).reference(etsResults.get(num).getReference())
                        .description(etsResults.get(num).getDescription())
                        .build())
        );

        return EmissionPoints.builder()
                .emissionPoints(points)
                .build();
    }

}
