package uk.gov.pmrv.api.migration.permit.emissionsummaries;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmissionSummariesSectionMigrationService implements PermitSectionMigrationService<EmissionSummaries>{

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE =
            "with XMLNAMESPACES ( \r\n" + 
            "    'urn:www-toplev-com:officeformsofd' AS fd \r\n" + 
            "), allPermits as ( \r\n" + 
            "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID \r\n" + 
            "      from tblForm F \r\n" + 
            "      join tblFormData FD on FD.fldFormID = F.fldFormID \r\n" + 
            "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID \r\n" + 
            "      join tlkpFormType FT on FTP.fldFormTypeID = FT.fldFormTypeID \r\n" + 
            "      join tlkpPhase P on FTP.fldPhaseID = P.fldPhaseID \r\n" + 
            "     where FD.fldMinorVersion = 0 \r\n" + 
            "       and P.fldDisplayName = 'Phase 3' \r\n" + 
            "       and FT.fldName = 'IN_PermitApplication' \r\n" + 
            "), mxPVer as ( \r\n" + 
            "    select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion' \r\n" + 
            "      from allPermits \r\n" + 
            "      group by fldFormID \r\n" + 
            "), latestPermit as ( \r\n" + 
            "    select e.fldEmitterID, e.fldEmitterDisplayId AS emitter_display_id, FD.* \r\n" + 
            "      from allPermits p \r\n" + 
            "      join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion \r\n" + 
            "      join tblFormData FD on FD.fldFormDataID = p.fldFormDataID \r\n" + 
            "      join tblEmitter E on E.fldEmitterID = p.fldEmitterID \r\n" + 
            "      join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" + 
            "), emissionSummaries as ( \r\n" + 
            "    select f.fldEmitterID as emitterId, \r\n" + 
            "           f.emitter_display_id as emitter_display_id, \r\n" + 
            "           T.c.query('Ia_annex_1_activity_emission_source_stream').value('.', 'NVARCHAR(MAX)') AS sourceStreamRef, \r\n" + 
            "           T.c.query('Ia_annex_1_activity_emission_source_refs').value('.', 'NVARCHAR(MAX)') AS emissionSourceRefs, \r\n" + 
            "           T.c.query('Ia_annex_1_activity_emission_point_refs').value('.', 'NVARCHAR(MAX)') AS emissionPointRefs, \r\n" + 
            "           T.c.query('Ia_annex_1_activity').value('.', 'NVARCHAR(MAX)') AS regulatedActivityRef, \r\n" + 
            "           0 as isExcludedRegulatedActivity \r\n" + 
            "      from latestPermit f \r\n" + 
            "     outer apply f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ia_emission_sources/row') T(c) \r\n" + 
            "    union all \r\n" + 
            "    select f.fldEmitterID as emitterId, \r\n" + 
            "           f.emitter_display_id as emitter_display_id, \r\n" + 
            "           T.c.query('Ia_excluded_activity_source_stream_refs').value('.', 'NVARCHAR(MAX)') AS sourceStreamRef, \r\n" + 
            "           T.c.query('Ia_excluded_activity_emission_source_refs').value('.', 'NVARCHAR(MAX)') AS emissionSourceRefs, \r\n" + 
            "           T.c.query('Ia_excluded_activity_emission_point_refs').value('.', 'NVARCHAR(MAX)') AS emissionPointRefs, \r\n" + 
            "           null as regulatedActivityRef, \r\n" + 
            "           1 as isExcludedRegulatedActivity \r\n" + 
            "      from latestPermit f \r\n" + 
            "     outer apply f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ia_excluded_activities/row') T(c) \r\n" + 
            "     where f.fldSubmittedXML.value('(fd:formdata/fielddata/Ia_installation_has_non_scheduled_activities)[1]', 'NVARCHAR(max)') = 'Yes' \r\n" + 
            "), r1 as ( \r\n" + 
            "    select emitterId, emitter_display_id, trim(value) sourceStreamRef, emissionSourceRefs, emissionPointRefs, regulatedActivityRef, isExcludedRegulatedActivity \r\n" + 
            "      from emissionSummaries \r\n" + 
            "      cross apply string_split(sourceStreamRef, ',') \r\n" + 
            "), r2 as ( \r\n" + 
            "    select emitterId, emitter_display_id, trim(value) sourceStreamRef, emissionSourceRefs, emissionPointRefs, regulatedActivityRef, isExcludedRegulatedActivity \r\n" + 
            "      from r1 \r\n" + 
            "      cross apply string_split(sourceStreamRef, '&') \r\n" + 
            ") \r\n" + 
            "select emitterId, emitter_display_id, sourceStreamRef, emissionSourceRefs, emissionPointRefs, regulatedActivityRef, isExcludedRegulatedActivity \r\n" + 
            "  from r2 ";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, List<EtsEmissionSummary>> etsEmissionSummaries = executeQuery(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        etsEmissionSummaries.forEach((etsAccountId, emissionSummaryList) ->
        Optional.ofNullable(accountsToMigratePermit.get(etsAccountId)).ifPresent(
            account -> {
                Permit permit = permits.get(account.getId()).getPermitContainer().getPermit();
                EmissionSummaries emissionSummaries = EmissionSummariesMigrationMapper.toEmissionSummaries(emissionSummaryList, permit);
                permit.setEmissionSummaries(emissionSummaries);
            }
        ));    
    }
    
    @Override
    public Map<String, EmissionSummaries> queryEtsSection(List<String> etsAccountIds) {
        throw new UnsupportedOperationException();
    }

    private Map<String, List<EtsEmissionSummary>> executeQuery(List<String> etsAccountIds) {
        String query = constructEtsEmissionSummariesSectionQuery(etsAccountIds);
        List<EtsEmissionSummary> etsEmissionSummaries = migrationJdbcTemplate.query(query,
                new EtsEmissionSummaryRowMapper(),
                etsAccountIds.isEmpty() ? new Object[] {} : etsAccountIds.toArray());
        return etsEmissionSummaries
            .stream()
            .collect(Collectors.groupingBy(EtsEmissionSummary::getEtsAccountId));
    }

    private String constructEtsEmissionSummariesSectionQuery(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where emitterId in (%s)", inAccountIdsSql));
        }
        return queryBuilder.toString();
    }

}
