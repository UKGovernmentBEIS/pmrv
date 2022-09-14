package uk.gov.pmrv.api.migration.permit.regulatedactivities;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.MigrationPermitHelper;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class RegulatedActivitiesSectionMigrationService implements PermitSectionMigrationService<RegulatedActivities> {
    
    private final JdbcTemplate migrationJdbcTemplate;

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
            "e.fldEmitterID as emitterId, \r\n" +
            "T.c.query('Ia_annex_1_activity').value('.', 'NVARCHAR(MAX)') AS type, \r\n" +
            "T.c.query('Ia_annex_1_activity_capacity').value('.', 'NVARCHAR(MAX)') AS capacity, \r\n" +
            "T.c.query('Ia_annex_1_activity_capacity_units').value('.', 'NVARCHAR(MAX)') AS capacityUnit, \r\n" +
            "T.c.query('Ia_annex_1_activity_specified_emissions').value('.', 'NVARCHAR(MAX)') AS greenHousesCategory \r\n" +
            "from tblEmitter e \r\n" +
            "join tlkpEmitterStatus es on e.fldEmitterStatusID = es.fldEmitterStatusID and es.fldDisplayName = 'Live' \r\n" +
            "join latestPermit f       on e.fldEmitterID = f.fldEmitterID \r\n" +
            "OUTER APPLY f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ia_annex_1_activities/row') T(c) \r\n"
            ;
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, RegulatedActivities> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId()).getPermitContainer().getPermit()
                        .setRegulatedActivities(section));
    }
    
    @Override
    public Map<String, RegulatedActivities> queryEtsSection(List<String> accountIds) {
        String query = MigrationPermitHelper.constructEtsSectionQuery(QUERY_BASE, accountIds);
        
        Map<String, List<RegulatedActivity>> accountRegulatedActivities = executeQuery(query, accountIds);
        return accountRegulatedActivities.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, e -> RegulatedActivitiesMapper.toPmrvRegulatedActivities(e.getValue())));
    }
    
    private Map<String, List<RegulatedActivity>> executeQuery(String query, List<String> accountIds) {
        List<RegulatedActivity> accountRegulatedActivities = migrationJdbcTemplate.query(query,
                new RegulatedActivityRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return accountRegulatedActivities
                .stream()
                .collect(Collectors.groupingBy(RegulatedActivity::getEtsAccountId));
    }

}
