package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.SamplingPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class CalculationMonitoringApproachesDetailsMigrationService implements PermitSectionMigrationService<CalculationMonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;
    private final EtsPermitFileAttachmentQueryService etsPermitFileAttachmentQueryService;
    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

    private static final String QUERY_BASE  =
            "with XMLNAMESPACES (\r\n" +
                    "'urn:www-toplev-com:officeformsofd' AS fd\r\n" +
                    "), allPermits as (\r\n" +
                    "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID,\r\n" +
                    "           FD.fldMajorVersion versionKey\r\n" +
                    "           --, format(P.fldDisplayID, '00') + '|' + format(FD.fldMajorVersion, '0000') + '|' + format(FD.fldMinorVersion, '0000')  versionKey\r\n" +
                    "      from tblForm F\r\n" +
                    "      join tblFormData FD        on FD.fldFormID = F.fldFormID\r\n" +
                    "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n" +
                    "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\r\n" +
                    "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\r\n" +
                    "     where P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\r\n" +
                    "), latestVersion as (\r\n" +
                    "    select fldEmitterID, max(versionKey) MaxVersionKey from allPermits group by fldEmitterID\r\n" +
                    "), latestPermit as (\r\n" +
                    "    select p.fldEmitterID, FD.*\r\n" +
                    "  from allPermits p\r\n" +
                    "  join latestVersion v on v.fldEmitterID = p.fldEmitterID and p.fldMajorVersion = v.MaxVersionKey and p.fldMinorVersion = 0 \r\n" +
                    "  join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\r\n" +
                    "  join tblEmitter E   on E.fldEmitterID = p.fldEmitterID\r\n" +
                    "  join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live'\r\n" +
                    ")\r\n" +
                    "select E.fldEmitterID,\r\n" +
                    "-- Approach Description\r\n" +
                    "trim(fldSubmittedXML.value('(fd:formdata/fielddata/Calc_calculation_approach_description)[1]', 'NVARCHAR(max)')) Calc_calculation_approach_description,\r\n" +
                    "-- Undertake Sampling and Analysis?  Yes/No\r\n" +
                    "trim(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_undertake_sampling_analysis)[1]', 'NVARCHAR(max)')) Cf_undertake_sampling_analysis,\r\n" +
                    "-- Analysis procedures - If Cf_undertake_sampling_analysis = 'Yes'\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_title                         )[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_title,\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_reference,\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_diagram_reference,\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_description                   )[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_description,\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_responsible_post_department,\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_location                      )[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_location,\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_it_system,\r\n" +
                    "nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_for_analyses-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'') Cf_procedures_for_analyses_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Sampling plan - If Cf_undertake_sampling_analysis = 'Yes'\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_title,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_reference,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_diagram_reference,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_description,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_responsible_post_department,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_location,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_it_system,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_analyses_sampling_plan-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Cf_procedures_analyses_sampling_plan_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Sampling plan appropriateness - If Cf_undertake_sampling_analysis = 'Yes'\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_title,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_reference,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_diagram_reference,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_description,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_responsible_post_department,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_location,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_it_system,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_revise_sampling_plan_appropriateness-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Cf_procedures_revise_sampling_plan_appropriateness_Procedure_cen_or_other_standards_applied,\r\n" +
                    "-- Reconciliation? Yes/No - If Cf_undertake_sampling_analysis = 'Yes'\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_reconciliations)[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_reconciliations,\r\n" +
                    "-- Year end reconciliation - If Cf_undertake_sampling_analysis = 'Yes' and Cf_procedures_year_reconciliations = 'Yes'\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_title,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_reference,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_diagram_reference,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_description,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_responsible_post_department,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_location,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_it_system,\r\n" +
                    "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Cf_procedures_year_end_reconcilation-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Cf_procedures_year_end_reconcilation_Procedure_cen_or_other_standards_applied\r\n" +
                    "--, fldSubmittedXML\r\n" +
                    " from tblEmitter E\r\n" +
                    "     join latestPermit F on E.fldEmitterID = F.fldEmitterID\r\n" +
                    " where fldSubmittedXML.value('(fd:formdata/fielddata/Ma_calculation)[1]', 'NVARCHAR(max)') = 'Yes'\r\n"
            ;

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, List<EtsFileAttachment>> attachments = etsPermitFileAttachmentQueryService
                .query(accountIds, EtsFileAttachmentType.SAMPLING_PLAN);

        Map<String, CalculationMonitoringApproach> sections = queryEtsSection(accountIds);

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());
            PermitContainer permitContainer = permitMigrationContainer.getPermitContainer();

            CalculationMonitoringApproach calculationMonitoringApproach =
                    (CalculationMonitoringApproach)permitMigrationContainer
                    .getPermitContainer()
                    .getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(MonitoringApproachType.CALCULATION);
            calculationMonitoringApproach.setApproachDescription(section.getApproachDescription());
            SamplingPlan samplingPlan = section.getSamplingPlan();
            calculationMonitoringApproach.setSamplingPlan(samplingPlan);
            if (samplingPlan.getDetails() != null && samplingPlan.getDetails().getProcedurePlan() != null) {
                Set<UUID> attachmentsUuids = attachments.getOrDefault(etsAccId, List.of()).stream().map(EtsFileAttachment::getUuid).collect(Collectors.toSet());
                samplingPlan.getDetails().getProcedurePlan().setProcedurePlanIds(attachmentsUuids);
            }

            attachments.getOrDefault(etsAccId, List.of()).forEach(file ->
                    permitContainer.getPermitAttachments().put(file.getUuid(), file.getUploadedFileName()));
            permitMigrationContainer.getFileAttachments()
                    .addAll(etsFileAttachmentMapper.toFileAttachments(attachments.getOrDefault(etsAccId, List.of())));
        });
    }

    @Override
    public Map<String, CalculationMonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("and e.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();

        Map<String, EtsCalculationMonitoringApproachDetails> etsCalculationMonitoringApproaches = executeQuery(query, accountIds);

        Map<String, CalculationMonitoringApproach> calculationMonitoringApproaches = new HashMap<>();
        etsCalculationMonitoringApproaches.forEach((etsAccountId, calculationMonitoringApproach) -> {
            String approachDescription = CalculationApproachDetailsMapper.constructCalculationApproachDescription(calculationMonitoringApproach);
            SamplingPlan samplingPlan = CalculationApproachDetailsMapper.constructCalculationSamplingPlan(calculationMonitoringApproach);
            calculationMonitoringApproaches.put(etsAccountId,
                    CalculationMonitoringApproach.builder()
                            .approachDescription(approachDescription)
                            .samplingPlan(samplingPlan)
                            .build());
        });
        return calculationMonitoringApproaches;
    }

    private Map<String, EtsCalculationMonitoringApproachDetails> executeQuery(String query, List<String> accountIds) {
        List<EtsCalculationMonitoringApproachDetails> etsCalculationMonitoringApproachesDetails = migrationJdbcTemplate.query(query,
                new EtsCalculationMonitoringApproachDetailsRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsCalculationMonitoringApproachesDetails
                .stream()
                .collect(Collectors.toMap(EtsCalculationMonitoringApproachDetails::getEtsAccountId,
                        etsCalculationMonitoringApproachDetails -> etsCalculationMonitoringApproachDetails));
    }
}
