package uk.gov.pmrv.api.migration.permit.monitoringreporting;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentType;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.attachments.EtsPermitFileAttachmentQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringRole;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MonitoringReportingMigrationService implements PermitSectionMigrationService<MonitoringReporting> {

    private final JdbcTemplate migrationJdbcTemplate;
    private final EtsPermitFileAttachmentQueryService etsPermitFileAttachmentQueryService;
    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

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
            "T.c.query('Man_job_title_post').value('.', 'NVARCHAR(MAX)') AS jobTitle, " +
            "T.c.query('Man_responsibilities').value('.', 'NVARCHAR(MAX)') AS mainDuties " +
            "from tblEmitter E " +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' " +
            "join latestPermit F on E.fldEmitterID = F.fldEmitterID " +
            "OUTER APPLY f.fldSubmittedXML.nodes('fd:formdata/fielddata/Man_monitoring_reporting_responsibilities/row') T(c)";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
            Map<Long, PermitMigrationContainer> permits) {

        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        // Create sql statement
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);

        Map<String, List<EtsMonitoringReporting>> results = migrationJdbcTemplate.query(query,
                new MonitoringReportingMigrationRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray()).stream().collect(Collectors.groupingBy(EtsMonitoringReporting::getEmitterId));
        Map<String, List<EtsFileAttachment>> attachments = etsPermitFileAttachmentQueryService
                .query(accountIds, EtsFileAttachmentType.MONITORING_REPORTING);

        Map<String, MonitoringReporting> sections = results.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> transform(e.getValue(), attachments.getOrDefault(e.getKey(), List.of()))));

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());
            PermitContainer permitContainer = permitMigrationContainer.getPermitContainer();

            permitContainer.getPermit().getManagementProcedures().setMonitoringReporting(section);

            attachments.getOrDefault(etsAccId, List.of()).forEach(file ->
                    permitContainer.getPermitAttachments().put(file.getUuid(), file.getUploadedFileName()));
            permitMigrationContainer.getFileAttachments()
                    .addAll(etsFileAttachmentMapper.toFileAttachments(attachments.getOrDefault(etsAccId, List.of())));
        });
    }

    @Override
    public Map<String, MonitoringReporting> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }

    private MonitoringReporting transform(List<EtsMonitoringReporting> etsResults, List<EtsFileAttachment> attachments) {
        List<MonitoringRole> roles = etsResults.stream().map(ets -> MonitoringRole.builder().jobTitle(ets.getJobTitle())
                .mainDuties(ets.getMainDuties())
                .build()).collect(Collectors.toList());

        return MonitoringReporting.builder()
                .monitoringRoles(roles)
                .organisationCharts(attachments.stream().map(EtsFileAttachment::getUuid).collect(Collectors.toSet()))
                .build();
    }
}
