package uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurement;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MeasMonitoringApproachesDetailsMigrationService implements PermitSectionMigrationService<MeasMonitoringApproach> {
    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE  = "/* Get Measurement approach details */\r\n" +
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
            "trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_measurment_approach_description)[1]', 'NVARCHAR(max)'),'')) Meas_measurment_approach_description,\r\n" +
            "            -- Determination of Emissions by Measurement\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_title                         ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_reference                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_diagram_reference             ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_description                   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_responsible_post_department   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_location                      ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_it_system                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_emissions_determination-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Meas_proc_emissions_determination_Procedure_cen_or_other_standards_applied,\r\n" +
            "            -- Determination of Reference Periods\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_title                         ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_reference                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_diagram_reference             ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_description                   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_responsible_post_department   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_location                      ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_it_system                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_reference_periods_substitution_missing_data-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Meas_proc_reference_periods_substitution_missing_data_Procedure_cen_or_other_standards_applied,\r\n" +
            "            -- Gas Flow Determination\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow_determined_by_calculation)[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_determined_by_calculation,\r\n" +
            "            -- Gas Flow Determination procedure - If Meas_proc_gas_flow_determined_by_calculation = 'Yes'\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_title                         ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_reference                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_diagram_reference             ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_description                   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_responsible_post_department   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_location                      ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_it_system                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_gas_flow-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Meas_proc_gas_flow_Procedure_cen_or_other_standards_applied,\r\n" +
            "            -- Biomass Emissions Deduction\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_biomass_emissions_deducted)[1]', 'NVARCHAR(max)'),'')) Meas_proc_biomass_emissions_deducted,\r\n" +
            "            -- Biomass Emissions Deduction procedure - If Meas_proc_biomass_emissions_deducted = 'Yes'\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_title                         ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_reference                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_diagram_reference             ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_description                   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_responsible_post_department   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_location                      ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_it_system                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_deduction_of_biomass_emissions-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Meas_proc_deduction_of_biomass_emissions_Procedure_cen_or_other_standards_applied,\r\n" +
            "            -- Corroborating Calculations\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_title                         )[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_title                         ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_reference                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_reference                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_diagram_reference             )[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_diagram_reference             ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_description                   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_description                   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_responsible_post_department   )[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_responsible_post_department   ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_location                      )[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_location                      ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_it_system                     )[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_it_system                     ,\r\n" +
            "            trim(nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Meas_proc_corroborating_calculations-Procedure_cen_or_other_standards_applied)[1]', 'NVARCHAR(max)'),'')) Meas_proc_corroborating_calculations_Procedure_cen_or_other_standards_applied\r\n" +
            "            --, fldSubmittedXML\r\n" +
            " from tblEmitter E\r\n" +
            "     join latestPermit F on E.fldEmitterID = F.fldEmitterID\r\n" +
            "where fldSubmittedXML.value('(fd:formdata/fielddata/Ma_measurement)[1]', 'NVARCHAR(max)') = 'Yes'\r\n";
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        Map<String, MeasMonitoringApproach> sections = queryEtsSection(accountIds);

        sections.forEach((etsAccId, section) -> {
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            MeasMonitoringApproach measMonitoringApproach =
                    (MeasMonitoringApproach)permitMigrationContainer
                            .getPermitContainer()
                            .getPermit()
                            .getMonitoringApproaches()
                            .getMonitoringApproaches()
                            .get(MonitoringApproachType.MEASUREMENT);
            measMonitoringApproach.setApproachDescription(section.getApproachDescription());
            measMonitoringApproach.setEmissionDetermination(section.getEmissionDetermination());
            measMonitoringApproach.setReferencePeriodDetermination(section.getReferencePeriodDetermination());
            measMonitoringApproach.setGasFlowCalculation(section.getGasFlowCalculation());
            measMonitoringApproach.setBiomassEmissions(section.getBiomassEmissions());
            measMonitoringApproach.setCorroboratingCalculations(section.getCorroboratingCalculations());
        });
    }

    @Override
    public Map<String, MeasMonitoringApproach> queryEtsSection(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("and e.fldEmitterID in (%s)", inAccountIdsSql));
        }
        String query = queryBuilder.toString();

        Map<String, EtsMeasMonitoringApproachDetails> etsMeasMonitoringApproachDetailsMap = executeQuery(query, accountIds);

        Map<String, MeasMonitoringApproach> measMonitoringApproaches = new HashMap<>();
        etsMeasMonitoringApproachDetailsMap.forEach((etsAccountId, etsMeasMonitoringApproachDetails) -> {
            String approachDescription = MeasApproachDetailsMapper.constructMeasApproachDescription(etsMeasMonitoringApproachDetails);
            ProcedureForm emissionDetermination = MeasApproachDetailsMapper.constructEmissionDetermination(etsMeasMonitoringApproachDetails);
            ProcedureForm referencePeriodDetermination = MeasApproachDetailsMapper.constructReferencePeriodDetermination(etsMeasMonitoringApproachDetails);
            ProcedureOptionalForm gasFlowCalculation = MeasApproachDetailsMapper.constructGasFlowCalculation(etsMeasMonitoringApproachDetails);
            ProcedureOptionalForm biomassEmissions = MeasApproachDetailsMapper.constructBiomassEmissions(etsMeasMonitoringApproachDetails);
            ProcedureForm corroboratingCalculations = MeasApproachDetailsMapper.constructCorroboratingCalculations(etsMeasMonitoringApproachDetails);
            measMonitoringApproaches.put(etsAccountId,
                    MeasMonitoringApproach.builder()
                            .approachDescription(approachDescription)
                            .emissionDetermination(emissionDetermination)
                            .referencePeriodDetermination(referencePeriodDetermination)
                            .gasFlowCalculation(gasFlowCalculation)
                            .biomassEmissions(biomassEmissions)
                            .corroboratingCalculations(corroboratingCalculations)
                            .build());
        });
        return measMonitoringApproaches;
    }

    private Map<String, EtsMeasMonitoringApproachDetails> executeQuery(String query, List<String> accountIds) {
        List<EtsMeasMonitoringApproachDetails> etsCalculationMonitoringApproachesDetails = migrationJdbcTemplate.query(query,
                new EtsMeasMonitoringApproachDetailsRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsCalculationMonitoringApproachesDetails
                .stream()
                .collect(Collectors.toMap(EtsMeasMonitoringApproachDetails::getEtsAccountId,
                        etsCalculationMonitoringApproachDetails -> etsCalculationMonitoringApproachDetails));
    }
}
