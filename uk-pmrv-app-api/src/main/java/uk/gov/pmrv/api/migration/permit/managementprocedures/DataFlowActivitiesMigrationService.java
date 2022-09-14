package uk.gov.pmrv.api.migration.permit.managementprocedures;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.managementprocedures.DataFlowActivities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class DataFlowActivitiesMigrationService implements PermitSectionMigrationService<DataFlowActivities> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE =
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
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_title)[1]', 'NVARCHAR(max)') AS procedureTitle, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_reference)[1]', 'NVARCHAR(max)') AS procedureReference, \n\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_diagram_reference)[1]', 'NVARCHAR(max)') AS diagramReference, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_description)[1]', 'NVARCHAR(max)') AS procedureDescription,\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_responsible_post_department)[1]', 'NVARCHAR(max)') AS responsiblePostOrDepartment, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_location)[1]', 'NVARCHAR(max)') AS procedureLocation, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_it_system)[1]', 'NVARCHAR(max)') AS procedureItSystem, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)') AS enOrOtherStandardsApplied, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities_primary_data_sources)[1]', 'NVARCHAR(max)') AS primaryDataSources, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_data_flow_activities_relevant_processing_steps)[1]', 'NVARCHAR(max)') AS relevantProcessingSteps \r\n" +
            "from tblEmitter E \r\n" +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" +
            "join latestPermit F       on E.fldEmitterID = F.fldEmitterID \r\n";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, DataFlowActivities> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setDataFlowActivities(section));
    }

    @Override
    public Map<String, DataFlowActivities> queryEtsSection(List<String> accountIds) {
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);
        List<EtsDataFlowActivities> etsDataFlowActivities = executeQuery(query, accountIds);

        return etsDataFlowActivities.stream()
            .collect(Collectors.toMap(EtsDataFlowActivities::getEmitterId,
                DataFlowActivitiesMapper::toDataFlowActivities));
    }

    private List<EtsDataFlowActivities> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsDataFlowActivitiesRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }
}
