package uk.gov.pmrv.api.migration.permit.sourcestreams;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.MigrationPermitHelper;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class SourceStreamsSectionMigrationService implements PermitSectionMigrationService<SourceStreams> {
    
    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE  = 
            "with XMLNAMESPACES (\r\n"
            + "    'urn:www-toplev-com:officeformsofd' AS fd\r\n"
            + "), allPermits as (\r\n"
            + "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID\r\n"
            + "      from tblForm F\r\n"
            + "      join tblFormData FD        on FD.fldFormID = F.fldFormID\r\n"
            + "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n"
            + "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\r\n"
            + "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\r\n"
            + "     where FD.fldMinorVersion = 0 and P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\r\n"
            + "), mxPVer as (\r\n"
            + "    select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion'\r\n"
            + "      from allPermits\r\n"
            + "     group by fldFormID\r\n"
            + "), latestPermit as (\r\n"
            + "    select p.fldEmitterID, FD.*\r\n"
            + "      from allPermits p\r\n"
            + "      join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion\r\n"
            + "      join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\r\n"
            + ") \r\n"
            + "select \r\n"
            + " e.fldEmitterID as emitterId, \r\n"
            + " T.c.query('Ed_source_stream_reference').value('.', 'NVARCHAR(MAX)') as reference,\r\n"
            + " T.c.query('Ed_source_stream_type').value('.', 'NVARCHAR(MAX)') as type,\r\n"
            + " T.c.query('Ed_source_stream_description').value('.', 'NVARCHAR(MAX)') as description\r\n"
            + " from tblEmitter E \r\n"
            + " join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live'\r\n"
            + " join latestPermit F       on E.fldEmitterID = F.fldEmitterID\r\n"
            + " cross APPLY fldSubmittedXML.nodes('(fd:formdata/fielddata/Ed_source_streams/row)') as T(c)\r\n"
            ;
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, SourceStreams> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setSourceStreams(section));        
    }
    
    @Override
    public Map<String, SourceStreams> queryEtsSection(List<String> accountIds) {
        String query = MigrationPermitHelper.constructEtsSectionQuery(QUERY_BASE, accountIds);
        
        Map<String, List<SourceStream>> accountSourceStreamsList = executeQuery(query, accountIds);
        Map<String, SourceStreams> accountSourceStreams = new HashMap<>();
        accountSourceStreamsList.forEach((etsAccountId, list) -> 
            SourceStreamsMapper.constructSourceStreams(list)
                .ifPresentOrElse(
                        ss -> accountSourceStreams.put(etsAccountId, ss),
                        () -> log.error("Source streams section ignored for account id: " + etsAccountId)));
        return accountSourceStreams;
    }
    
    private Map<String, List<SourceStream>> executeQuery(String query, List<String> accountIds) {
        List<SourceStream> accountSourceStreams = migrationJdbcTemplate.query(query,
                new SourceStreamRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return accountSourceStreams
                .stream()
                .collect(Collectors.groupingBy(SourceStream::getEtsAccountId));
    }

}
