package uk.gov.pmrv.api.migration.permit.managementprocedures;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.ASSESS_AND_CONTROL_RISKS;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.ASSIGNMENT_OF_RESPONSIBILITIES;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.CONTROL_OF_OUTSOURCED_ACTIVITIES;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.CORRECTIONS_AND_CORRECTIVE_ACTIONS;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.MONITORING_PLAN_APPROPRIATENESS;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.QA_DATA_FLOW_ACTIVITIES;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.QA_METERING_AND_MEASURING_EQUIPMENT;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.RECORD_KEEPING_AND_DOCUMENTATION;
import static uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresDefinitionType.REVIEW_AND_VALIDATION_OF_DATA;

import java.util.ArrayList;
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
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProceduresDefinition;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class ManagementProceduresDefinitionMigrationService implements PermitSectionMigrationService<ManagementProceduresDefinition> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final Map<ManagementProceduresDefinitionType, String> managementProceduresDefinitionTypeToQueryMap =
        Map.of(ASSESS_AND_CONTROL_RISKS, "Man_assess_and_control_risks",
            ASSIGNMENT_OF_RESPONSIBILITIES, "Man_assignment_of_responsibilities",
            CONTROL_OF_OUTSOURCED_ACTIVITIES, "Man_control_of_outsourced_activities",
            CORRECTIONS_AND_CORRECTIVE_ACTIONS, "Man_corrections_and_corrective_actions",
            MONITORING_PLAN_APPROPRIATENESS, "Man_monitoring_plan_appropriateness",
            QA_DATA_FLOW_ACTIVITIES, "Man_quality_assurance_information_technology",
            QA_METERING_AND_MEASURING_EQUIPMENT, "Man_quality_assurance_metering_measuring_equipment",
            RECORD_KEEPING_AND_DOCUMENTATION, "Man_management_of_record_keeping_and_documentation",
            REVIEW_AND_VALIDATION_OF_DATA, "Man_review_and_evaluation_of_data");

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
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_title)[1]', 'NVARCHAR(max)') AS procedureTitle, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_reference)[1]', 'NVARCHAR(max)') AS procedureReference, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_diagram_reference)[1]', 'NVARCHAR(max)') AS diagramReference, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_description)[1]', 'NVARCHAR(max)') AS procedureDescription, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_responsible_post_department)[1]', 'NVARCHAR(max)') AS responsiblePostOrDepartment, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_location)[1]', 'NVARCHAR(max)') AS procedureLocation, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_it_system)[1]', 'NVARCHAR(max)') AS procedureItSystem, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/%1$s-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)') AS enOrOtherStandardsApplied \r\n" +
            "from tblEmitter E \r\n" +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" +
            "join latestPermit F       on E.fldEmitterID = F.fldEmitterID \r\n";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        populateAssessAndControlRisks(accountsToMigratePermit, permits);
        populateAssignmentOfResponsibilities(accountsToMigratePermit, permits);
        populateControlOfOutsourcedActivities(accountsToMigratePermit, permits);
        populateCorrectionsAndCorrectiveActions(accountsToMigratePermit, permits);
        populateMonitoringPlanAppropriateness(accountsToMigratePermit, permits);
        populateQaDataFlowActivities(accountsToMigratePermit, permits);
        populateQaMeteringAndMeasuringEquipment(accountsToMigratePermit, permits);
        populateRecordKeepingAndDocumentation(accountsToMigratePermit, permits);
        populateReviewAndValidationOfData(accountsToMigratePermit, permits);
    }

    public Map<String, ManagementProceduresDefinition> queryEtsSection(List<String> accountIds, ManagementProceduresDefinitionType type) {
        String resolvedQuery = String.format(QUERY_BASE, managementProceduresDefinitionTypeToQueryMap.get(type));
        String finalQuery = constructEtsSectionQuery(resolvedQuery, accountIds);
        List<EtsManagementProceduresDefinition> etsManagementProceduresDefinitionList = executeQuery(finalQuery, accountIds);

        return etsManagementProceduresDefinitionList.stream()
            .collect(Collectors.toMap(EtsManagementProceduresDefinition::getEmitterId,
                ManagementProceduresMapper::toManagementProceduresProcessDefinition));
    }

    private List<EtsManagementProceduresDefinition> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsManagementProceduresRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }

    @Override
    public Map<String, ManagementProceduresDefinition> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }
    
    private void populateAssessAndControlRisks(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.ASSESS_AND_CONTROL_RISKS);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setAssessAndControlRisk(section));
    }
    
    private void populateAssignmentOfResponsibilities(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.ASSIGNMENT_OF_RESPONSIBILITIES);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setAssignmentOfResponsibilities(section));
    }
    
    private void populateControlOfOutsourcedActivities(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.CONTROL_OF_OUTSOURCED_ACTIVITIES);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setControlOfOutsourcedActivities(section));
    }
    
    private void populateCorrectionsAndCorrectiveActions(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.CORRECTIONS_AND_CORRECTIVE_ACTIONS);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setCorrectionsAndCorrectiveActions(section));
    }
    
    private void populateMonitoringPlanAppropriateness(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.MONITORING_PLAN_APPROPRIATENESS);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setMonitoringPlanAppropriateness(section));
    }
    
    private void populateQaDataFlowActivities(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.QA_DATA_FLOW_ACTIVITIES);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setQaDataFlowActivities(section));
    }
    
    private void populateQaMeteringAndMeasuringEquipment(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.QA_METERING_AND_MEASURING_EQUIPMENT);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setQaMeteringAndMeasuringEquipment(section));
    }
    
    private void populateRecordKeepingAndDocumentation(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.RECORD_KEEPING_AND_DOCUMENTATION);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setRecordKeepingAndDocumentation(section));
    }
    
    private void populateReviewAndValidationOfData(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, ManagementProceduresDefinition> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()), ManagementProceduresDefinitionType.REVIEW_AND_VALIDATION_OF_DATA);
        
        sections
            .forEach((etsAccId, section) ->
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().getManagementProcedures().setReviewAndValidationOfData(section));
    }
    
}
