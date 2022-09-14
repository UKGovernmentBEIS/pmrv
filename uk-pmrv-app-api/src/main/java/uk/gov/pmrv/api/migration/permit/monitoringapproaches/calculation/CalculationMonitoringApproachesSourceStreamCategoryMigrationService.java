package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import static uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation.CalculationParameter.BF;
import static uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation.CalculationParameter.CC;
import static uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation.CalculationParameter.CF;
import static uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation.CalculationParameter.EF;
import static uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation.CalculationParameter.NCV;
import static uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation.CalculationParameter.OxF;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationAnalysisMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationBiomassFractionStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationBiomassFractionStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationCarbonContent;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationCarbonContentStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationCarbonContentStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationConversionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationConversionFactorStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationConversionFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationEmissionFactorStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationEmissionFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationLaboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationNetCalorificValueStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationNetCalorificValueStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationOxidationFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationOxidationFactorStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationOxidationFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.ReducedSamplingFrequencyJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CalculationAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class CalculationMonitoringApproachesSourceStreamCategoryMigrationService implements PermitSectionMigrationService<CalculationMonitoringApproach> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String SOURCE_STREAM_CATEGORY_QUERY =
            "select \r\n" +
                "tier_id, fldEmitterID, \r\n" +
                "source_stream_ref, Calc_tiers_emission_source_refs as emission_source_refs, estimated_emission, \r\n" +
                "Calc_tiers_applied_monitoring_approach as monitoring_approach, Calc_tiers_source_category as source_category, \r\n" +
                "Calc_tiers_highest_tiers_applied_justification as justification, \r\n" +
                "Calc_tiers_measurement_device_refs as measurement_device_refs, \r\n" +
                "Calc_tiers_overall_metering_uncertainty as overall_metering_uncertainty, \r\n" +
                "ad_tier_applied, ad_is_maximum_tier, ad_highest_tier_applied, \r\n" +
                "ncv_tier_applied, ncv_is_maximum_tier, ncv_highest_tier_applied, \r\n" +
                "ef_tier_applied, ef_is_maximum_tier, ef_highest_tier_applied, \r\n" +
                "oxf_tier_applied, oxf_is_maximum_tier, oxf_highest_tier_applied, \r\n" +
                "cc_tier_applied, cc_is_maximum_tier, cc_highest_tier_applied, \r\n" +
                "cf_tier_applied, cf_is_maximum_tier, cf_highest_tier_applied, \r\n" +
                "bf_tier_applied, bf_is_maximum_tier, bf_highest_tier_applied \r\n" +
            "from mig_permit_calc_categories a";

    private static final String DEFAULT_VALUE_QUERY =
            "select \r\n" +
                "td.tier_id, d.fldEmitterID, d.defaults_parameter parameter, \r\n" +
                "d.Cf_default_value_reference_source reference_source, d.default_value \r\n" +
            "from mig_permit_calc_tiers_defaults td join mig_permit_calc_defaults d on d.defaults_id = td.defaults_id";

    private static final String ANALYSIS_QUERY = 
        "select \r\n" +
            "ta.tier_id, a.fldEmitterId, a.analysis_parameter parameter, \r\n" +
            "a.Cf_analysis_method_of_analysis method_of_analysis, a.analysis_frequency_other frequency_other, \r\n" +
            "a.Cf_analysis_frequency frequency, Case when a.Cf_analysis_frequency_less_mrr = 'Yes' then 'No' else 'Yes' end frequency_meet_mrr, \r\n" +
            "a.Cf_analysis_frequency_less_mrr_reason frequency_less_mrr_reason, a.Cf_analysis_laboratory_name laboratory_name, \r\n" +
            "a.Cf_analysis_lab_iso_accredited is_lab_iso_accredited --, a.Cf_analysis_quality_assurance_measures \r\n" +
        "from mig_permit_calc_tiers_analysis ta join mig_permit_calc_analysis a on a.analysis_id = ta.analysis_id";

    private static final String UNREASONABLE_COSTS = "Unreasonable costs";
    private static final String ONE_THIRD_UNCERTAINTY = "Operator meets one third uncertainty criteria";

    @Override
    public void populateSection(final Map<String, Account> accountsToMigratePermit,
                                final Map<Long, PermitMigrationContainer> permits) {

        final List<String> accountIds = new ArrayList<>(accountsToMigratePermit.keySet());

        final Map<String, CalculationMonitoringApproach> sections = this.queryEtsSection(accountIds);

        this.replaceReferencesWithIds(sections, permits, accountsToMigratePermit);

        sections.forEach((etsAccId, section) -> {
            final PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());

            final CalculationMonitoringApproach calculationMonitoringApproach =
                    (CalculationMonitoringApproach)permitMigrationContainer
                    .getPermitContainer()
                    .getPermit()
                    .getMonitoringApproaches()
                    .getMonitoringApproaches()
                    .get(MonitoringApproachType.CALCULATION);
            calculationMonitoringApproach.setSourceStreamCategoryAppliedTiers(
                sections.get(etsAccId).getSourceStreamCategoryAppliedTiers()
            );
        });
    }

    @Override
    public Map<String, CalculationMonitoringApproach> queryEtsSection(final List<String> accountIds) {
        
        // source stream categories
        final String sourceStreamCategoryQuery = this.getSourceStreamCategoryQuery(accountIds);
        final Map<String, List<EtsCalculationSourceStreamCategory>> allCalculationSourceStreamCategories = 
            this.executeSourceStreamCategoryQuery(sourceStreamCategoryQuery, accountIds);
        
        // default values
        final String defaultValueQuery = this.getDefaultValueQuery(accountIds);
        final Map<String, List<EtsCalculationDefaultValue>> allDefaultValues =
            this.executeDefaultValueQuery(defaultValueQuery, accountIds);
        
        // analysis methods
        final String analysisQuery = this.getAnalysisMethodQuery(accountIds);
        final Map<String, List<EtsCalculationAnalysisMethod>> allAnalysisMethods =
            this.executeAnalysisQuery(analysisQuery, accountIds);

        final Map<String, CalculationMonitoringApproach> calculationMonitoringApproaches = new HashMap<>();
        allCalculationSourceStreamCategories.forEach((etsAccountId, sourceStreams) -> {
            final List<CalculationSourceStreamCategoryAppliedTier> appliedTiers = sourceStreams.stream().map(
                sourceStream -> {
                    
                    final List<EtsCalculationDefaultValue> defaultValues = allDefaultValues.get(etsAccountId);
                    final List<EtsCalculationAnalysisMethod> analysisMethods = allAnalysisMethods.get(etsAccountId);
                    final String tierId = sourceStream.getTierId();

                    final String ncvTierApplied = sourceStream.getNetCalorificValueTierApplied();
                    final boolean ncvExists = ncvTierApplied != null;
                    final CalculationNetCalorificValueStandardReferenceSource ncvStandardReferenceSource =
                        !ncvExists ? null : this.getNCVStandardReferenceSource(tierId, defaultValues);
                    final List<CalculationAnalysisMethod> ncvAnalysisMethods = !ncvExists ? 
                        Collections.emptyList() : this.getAnalysisMethod(tierId, NCV.name(), analysisMethods);

                    final String efTierApplied = sourceStream.getEmissionFactorTierApplied();
                    final boolean efExists = efTierApplied != null;
                    final CalculationEmissionFactorStandardReferenceSource efStandardReferenceSource =
                        !efExists ? null : this.getEFStandardReferenceSource(tierId, defaultValues);
                    final List<CalculationAnalysisMethod> efAnalysisMethods = !efExists ?
                        Collections.emptyList() : this.getAnalysisMethod(tierId, EF.name(), analysisMethods);

                    final String oxfTierApplied = sourceStream.getOxidationFactorTierApplied();
                    final boolean oxfExists = oxfTierApplied != null;
                    final CalculationOxidationFactorStandardReferenceSource oxfStandardReferenceSource =
                        !oxfExists ? null : this.getOxFStandardReferenceSource(tierId, defaultValues);
                    final List<CalculationAnalysisMethod> oxfAnalysisMethods = !oxfExists ?
                        Collections.emptyList() : this.getAnalysisMethod(tierId, OxF.name(), analysisMethods);

                    final String ccTierApplied = sourceStream.getCarbonContentTierApplied();
                    final boolean ccExists = ccTierApplied != null;
                    final CalculationCarbonContentStandardReferenceSource ccStandardReferenceSource =
                        !ccExists ? null : this.getCCStandardReferenceSource(tierId, defaultValues);
                    final List<CalculationAnalysisMethod> ccAnalysisMethods = !ccExists ?
                        Collections.emptyList() : this.getAnalysisMethod(tierId, CC.name(), analysisMethods);

                    final String cfTierApplied = sourceStream.getConversionFactorTierApplied();
                    final boolean cfExists = cfTierApplied != null;
                    final CalculationConversionFactorStandardReferenceSource cfStandardReferenceSource =
                        !cfExists ? null : this.getCFStandardReferenceSource(tierId, defaultValues);
                    final List<CalculationAnalysisMethod> cfAnalysisMethods = !cfExists ?
                        Collections.emptyList() : this.getAnalysisMethod(tierId, CF.name(), analysisMethods);

                    final String bfTierApplied = sourceStream.getBiomassFractionTierApplied();
                    final boolean bfExists = bfTierApplied != null;
                    final CalculationBiomassFractionStandardReferenceSource bfStandardReferenceSource =
                        !bfExists ? null : this.getBFStandardReferenceSource(tierId, defaultValues);
                    final List<CalculationAnalysisMethod> bfAnalysisMethods = !bfExists ?
                        Collections.emptyList() : this.getAnalysisMethod(tierId, BF.name(), analysisMethods);
                    
                    return CalculationSourceStreamCategoryAppliedTier.builder()
                        
                        .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                            .sourceStream(sourceStream.getSourceStream())
                            .emissionSources(sourceStream.getEmissionSources())
                            .annualEmittedCO2Tonnes(new BigDecimal(sourceStream.getEstimatedEmission()))
                            .categoryType(StringToEnumConverter.sourceStreamCategoryType(sourceStream.getSourceStreamCategory()))
                            .calculationMethod(StringToEnumConverter.calculationMethod(sourceStream.getCalculationMethod()))
                            .build())
                        
                        .activityData(CalculationActivityData.builder()
                            .measurementDevicesOrMethods(sourceStream.getMeasurementDevices())
                            .uncertainty(StringToEnumConverter.meteringUncertainty(sourceStream.getMeteringUncertainty()))
                            .tier(StringToEnumConverter.calculationActivityDataTier(sourceStream.getActivityDataTierApplied()))
                            .highestRequiredTier(sourceStream.isActivityDataIsMaxTier() ? null : 
                                HighestRequiredTier.builder()
                                .isHighestRequiredTier(sourceStream.isActivityDataIsHighestTier())
                                .noHighestRequiredTierJustification(
                                    this.getTierJustification(sourceStream.isActivityDataIsHighestTier(), 
                                                              sourceStream.getTierJustification())
                                )
                                .build())
                            .build())
                        
                        .netCalorificValue(CalculationNetCalorificValue.builder()
                            .exist(ncvExists)
                            .tier(StringToEnumConverter.calculationNetCalorificValueTier(ncvTierApplied))
                            .highestRequiredTier(!ncvExists || sourceStream.isNetCalorificValueIsMaxTier() ?
                                null : HighestRequiredTier.builder()
                                .isHighestRequiredTier(sourceStream.isNetCalorificValueIsHighestTier())
                                .noHighestRequiredTierJustification(
                                    this.getTierJustification(sourceStream.isNetCalorificValueIsHighestTier(),
                                                              sourceStream.getTierJustification())
                                )
                                .build())
                            .defaultValueApplied(!ncvExists ? null : ncvStandardReferenceSource != null)
                            .standardReferenceSource(ncvStandardReferenceSource)
                            .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                .analysisMethodUsed(!ncvExists ? null : !ncvAnalysisMethods.isEmpty())
                                .analysisMethods(ncvAnalysisMethods)
                                .build())
                            .build())
                        
                        .emissionFactor(CalculationEmissionFactor.builder()
                            .exist(efExists)
                            .tier(StringToEnumConverter.calculationEmissionFactorTier(efTierApplied))
                            .oneThirdRule(sourceStream.isEmissionFactorIsMaxTier() ? Boolean.FALSE : null)
                            .highestRequiredTier(!efExists || sourceStream.isEmissionFactorIsMaxTier() ?
                                null : HighestRequiredTier.builder()
                                .isHighestRequiredTier(sourceStream.isEmissionFactorIsHighestTierApplied())
                                .noHighestRequiredTierJustification(
                                    this.getTierJustification(sourceStream.isEmissionFactorIsHighestTierApplied(),
                                                              sourceStream.getTierJustification())
                                )
                                .build())
                            .defaultValueApplied(!efExists ? null : efStandardReferenceSource != null)
                            .standardReferenceSource(efStandardReferenceSource)
                            .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                .analysisMethodUsed(!efExists ? null : !efAnalysisMethods.isEmpty())
                                .analysisMethods(efAnalysisMethods)
                                .build())
                            .build())
                        
                        .oxidationFactor(CalculationOxidationFactor.builder()
                            .exist(oxfExists)
                            .tier(StringToEnumConverter.calculationOxidationFactorTier(oxfTierApplied))
                            .highestRequiredTier(!oxfExists || sourceStream.isOxidationFactorIsMaxTier() ?
                                null : HighestRequiredTier.builder()
                                .isHighestRequiredTier(sourceStream.isOxidationFactorIsHighestTierApplied())
                                .noHighestRequiredTierJustification(
                                    this.getTierJustification(sourceStream.isOxidationFactorIsHighestTierApplied(),
                                                              sourceStream.getTierJustification())
                                )
                                .build())
                            .defaultValueApplied(!oxfExists ? null : oxfStandardReferenceSource != null)
                            .standardReferenceSource(oxfStandardReferenceSource)
                            .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                .analysisMethodUsed(!oxfExists ? null : !oxfAnalysisMethods.isEmpty())
                                .analysisMethods(oxfAnalysisMethods)
                                .build())
                            .build())
                        
                        .carbonContent(CalculationCarbonContent.builder()
                            .exist(ccExists)
                            .tier(StringToEnumConverter.calculationCarbonContentTier(ccTierApplied))
                            .oneThirdRule(sourceStream.isCarbonContentIsMaxTier() ? Boolean.FALSE : null)
                            .highestRequiredTier(!ccExists || sourceStream.isCarbonContentIsMaxTier() ?
                                null : HighestRequiredTier.builder()
                                .isHighestRequiredTier(sourceStream.isCarbonContentIsHighestTierApplied())
                                .noHighestRequiredTierJustification(
                                    this.getTierJustification(sourceStream.isCarbonContentIsHighestTierApplied(),
                                                              sourceStream.getTierJustification())
                                )
                                .build())
                            .defaultValueApplied(!ccExists ? null : ccStandardReferenceSource != null)
                            .standardReferenceSource(ccStandardReferenceSource)
                            .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                .analysisMethodUsed(!ccExists ? null : !ccAnalysisMethods.isEmpty())
                                .analysisMethods(ccAnalysisMethods)
                                .build())
                            .build())
                        
                        .conversionFactor(CalculationConversionFactor.builder()
                            .exist(cfExists)
                            .tier(StringToEnumConverter.calculationConversionFactorTier(cfTierApplied))
                            .highestRequiredTier(!cfExists || sourceStream.isConversionFactorIsMaxTier() ?
                                null : HighestRequiredTier.builder()
                                .isHighestRequiredTier(sourceStream.isConversionFactorIsHighestTierApplied())
                                .noHighestRequiredTierJustification(
                                    this.getTierJustification(sourceStream.isConversionFactorIsHighestTierApplied(),
                                                              sourceStream.getTierJustification())
                                )
                                .build())
                            .defaultValueApplied(!cfExists ? null : cfStandardReferenceSource != null)
                            .standardReferenceSource(cfStandardReferenceSource)
                            .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                .analysisMethodUsed(!cfExists ? null : !cfAnalysisMethods.isEmpty())
                                .analysisMethods(cfAnalysisMethods)
                                .build())
                            .build())
                        
                        .biomassFraction(CalculationBiomassFraction.builder()
                            .exist(bfExists)
                            .tier(StringToEnumConverter.calculationBiomassFractionTier(bfTierApplied))
                            .highestRequiredTier(!bfExists || sourceStream.isBiomassFractionIsMaxTier() ?
                                null : HighestRequiredTier.builder()
                                .isHighestRequiredTier(sourceStream.isBiomassFractionIsHighestTierApplied())
                                .noHighestRequiredTierJustification(
                                    this.getTierJustification(sourceStream.isBiomassFractionIsHighestTierApplied(),
                                                              sourceStream.getTierJustification())
                                )
                                .build())
                            .defaultValueApplied(!bfExists ? null : bfStandardReferenceSource != null)
                            .standardReferenceSource(bfStandardReferenceSource)
                            .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                .analysisMethodUsed(!bfExists ? null : !bfAnalysisMethods.isEmpty())
                                .analysisMethods(bfAnalysisMethods)
                                .build())
                            .build())
                        .build();
                }
            ).collect(Collectors.toList());
            final CalculationMonitoringApproach monitoringApproach = CalculationMonitoringApproach.builder()
                .sourceStreamCategoryAppliedTiers(appliedTiers)
                .build();
            calculationMonitoringApproaches.put(etsAccountId, monitoringApproach);
        });
        return calculationMonitoringApproaches;
    }

    private String getSourceStreamCategoryQuery(final List<String> accountIds) {
        final StringBuilder sourceStreamCategoryQueryBuilder = new StringBuilder(SOURCE_STREAM_CATEGORY_QUERY);
        if (!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            sourceStreamCategoryQueryBuilder.append(String.format(" where a.fldEmitterID in (%s)", inAccountIdsSql));
        }
        return sourceStreamCategoryQueryBuilder.toString();
    }

    private String getDefaultValueQuery(final List<String> accountIds) {
        final StringBuilder defaultValueQueryBuilder = new StringBuilder(DEFAULT_VALUE_QUERY);
        if (!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            defaultValueQueryBuilder.append(String.format(" where d.fldEmitterID in (%s)", inAccountIdsSql));
        }
        return defaultValueQueryBuilder.toString();
    }
    
    private String getAnalysisMethodQuery(final List<String> accountIds) {
        final StringBuilder analysisMethodsQueryBuilder = new StringBuilder(ANALYSIS_QUERY);
        if (!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            analysisMethodsQueryBuilder.append(String.format(" where a.fldEmitterID in (%s)", inAccountIdsSql));
        }
        return analysisMethodsQueryBuilder.toString();
    }

    private Map<String, List<EtsCalculationSourceStreamCategory>> executeSourceStreamCategoryQuery(final String query,
                                                                                                   final List<String> accountIds) {
        final List<EtsCalculationSourceStreamCategory> etsCalculationSourceStreamCategories =
            migrationJdbcTemplate.query(query,
                new EtsCalculationSourceStreamCategoryRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());

        return etsCalculationSourceStreamCategories
            .stream()
            .collect(Collectors.groupingBy(EtsCalculationSourceStreamCategory::getEtsAccountId));
    }

    private Map<String, List<EtsCalculationDefaultValue>> executeDefaultValueQuery(final String query,
                                                                                   final List<String> accountIds) {
        final List<EtsCalculationDefaultValue> etsCalculationDefaultValues =
            migrationJdbcTemplate.query(query,
                new EtsCalculationDefaultValueRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());

        return etsCalculationDefaultValues
            .stream()
            .collect(Collectors.groupingBy(EtsCalculationDefaultValue::getEtsAccountId));
    }

    private Map<String, List<EtsCalculationAnalysisMethod>> executeAnalysisQuery(final String query,
                                                                                 final List<String> accountIds) {
        final List<EtsCalculationAnalysisMethod> etsCalculationDefaultValues =
            migrationJdbcTemplate.query(query,
                new EtsCalculationAnalysisMethodRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());

        return etsCalculationDefaultValues
            .stream()
            .collect(Collectors.groupingBy(EtsCalculationAnalysisMethod::getEtsAccountId));
    }

    private CalculationNetCalorificValueStandardReferenceSource getNCVStandardReferenceSource(
        final String tierId,
        final List<EtsCalculationDefaultValue> defaultValues) {

        final Optional<EtsCalculationDefaultValue> optionalDefaultValue =
            this.getDefaultValueForTierAndParameter(tierId, defaultValues, NCV.name());

        return optionalDefaultValue.map(
            etsCalculationDefaultValue -> CalculationNetCalorificValueStandardReferenceSource.builder()
                .type(CalculationNetCalorificValueStandardReferenceSourceType.OTHER)
                .otherTypeDetails(etsCalculationDefaultValue.getReferenceSource())
                .defaultValue(etsCalculationDefaultValue.getDefaultValue())
                .build()).orElse(null);
    }

    private CalculationEmissionFactorStandardReferenceSource getEFStandardReferenceSource(
        final String tierId, 
        final List<EtsCalculationDefaultValue> defaultValues) {

        final Optional<EtsCalculationDefaultValue> optionalDefaultValue =
            this.getDefaultValueForTierAndParameter(tierId, defaultValues, EF.name());

        return optionalDefaultValue.map(
            etsCalculationDefaultValue -> CalculationEmissionFactorStandardReferenceSource.builder()
                .type(CalculationEmissionFactorStandardReferenceSourceType.OTHER)
                .otherTypeDetails(etsCalculationDefaultValue.getReferenceSource())
                .defaultValue(etsCalculationDefaultValue.getDefaultValue())
                .build()).orElse(null);
    }

    private CalculationOxidationFactorStandardReferenceSource getOxFStandardReferenceSource(
        final String tierId, 
        final List<EtsCalculationDefaultValue> defaultValues) {

        final Optional<EtsCalculationDefaultValue> optionalDefaultValue =
            this.getDefaultValueForTierAndParameter(tierId, defaultValues, OxF.name());

        return optionalDefaultValue.map(
            etsCalculationDefaultValue -> CalculationOxidationFactorStandardReferenceSource.builder()
                .type(CalculationOxidationFactorStandardReferenceSourceType.OTHER)
                .otherTypeDetails(etsCalculationDefaultValue.getReferenceSource())
                .defaultValue(etsCalculationDefaultValue.getDefaultValue())
                .build()).orElse(null);
    }

    private CalculationCarbonContentStandardReferenceSource getCCStandardReferenceSource(
        final String tierId, 
        final List<EtsCalculationDefaultValue> defaultValues) {

        final Optional<EtsCalculationDefaultValue> optionalDefaultValue =
            this.getDefaultValueForTierAndParameter(tierId, defaultValues, CC.name());

        return optionalDefaultValue.map(
            etsCalculationDefaultValue -> CalculationCarbonContentStandardReferenceSource.builder()
                .type(CalculationCarbonContentStandardReferenceSourceType.OTHER)
                .otherTypeDetails(etsCalculationDefaultValue.getReferenceSource())
                .defaultValue(etsCalculationDefaultValue.getDefaultValue())
                .build()).orElse(null);
    }

    private CalculationConversionFactorStandardReferenceSource getCFStandardReferenceSource(
        final String tierId, 
        final List<EtsCalculationDefaultValue> defaultValues) {
        
        final Optional<EtsCalculationDefaultValue> optionalDefaultValue =
            this.getDefaultValueForTierAndParameter(tierId, defaultValues, CF.name());

        return optionalDefaultValue.map(
            etsCalculationDefaultValue -> CalculationConversionFactorStandardReferenceSource.builder()
                .type(CalculationConversionFactorStandardReferenceSourceType.OTHER)
                .otherTypeDetails(etsCalculationDefaultValue.getReferenceSource())
                .defaultValue(etsCalculationDefaultValue.getDefaultValue())
                .build()).orElse(null);
    }

    private CalculationBiomassFractionStandardReferenceSource getBFStandardReferenceSource(
        final String tierId, 
        final List<EtsCalculationDefaultValue> defaultValues) {

        final Optional<EtsCalculationDefaultValue> optionalDefaultValue =
            this.getDefaultValueForTierAndParameter(tierId, defaultValues, BF.name());

        return optionalDefaultValue.map(
            etsCalculationDefaultValue -> CalculationBiomassFractionStandardReferenceSource.builder()
                .type(CalculationBiomassFractionStandardReferenceSourceType.OTHER)
                .otherTypeDetails(etsCalculationDefaultValue.getReferenceSource())
                .defaultValue(etsCalculationDefaultValue.getDefaultValue())
                .build()).orElse(null);
    }
    
    private Optional<EtsCalculationDefaultValue> getDefaultValueForTierAndParameter(final String tierId,
                                                                                    final List<EtsCalculationDefaultValue> allDefaultValues,
                                                                                    final String parameter) {
        if (ObjectUtils.isEmpty(allDefaultValues)) {
            return Optional.empty();
        }
        final List<EtsCalculationDefaultValue> defaultValues = allDefaultValues.stream()
            .filter(dv -> dv.getTierId().equals(tierId))
            .filter(dv -> dv.getParameter().equals(parameter))
            .collect(Collectors.toList());
        // the following exception should never be reached because of the transformation pre-process
        if (defaultValues.size() > 1) {
            throw new RuntimeException(String.format("Multiple default values found for tier id %s and parameter %s", tierId, parameter));
        }
        return defaultValues.size() == 1 ? Optional.of(defaultValues.get(0)) : Optional.empty();
    }

    private List<CalculationAnalysisMethod> getAnalysisMethod(final String tierId,
                                                              final String parameter,
                                                              final List<EtsCalculationAnalysisMethod> etsCalculationAnalysisMethods) {
        if (ObjectUtils.isEmpty(etsCalculationAnalysisMethods)) {
            return Collections.emptyList();    
        }
        
        return etsCalculationAnalysisMethods.stream()
            .filter(method -> method.getTierId().equals(tierId))
            .filter(method -> method.getParameter().equals(parameter))
            .map(m -> CalculationAnalysisMethod.builder()
                .analysis(m.getAnalysisMethod())
                .samplingFrequency(m.isOtherFrequency() ? CalculationSamplingFrequency.OTHER : 
                    StringToEnumConverter.calculationSamplingFrequency(m.getFrequency()))
                .samplingFrequencyOtherDetails(m.isOtherFrequency() ? m.getFrequency() : null)
                .frequencyMeetsMinRequirements(m.isFrequencyMeetsMinRequirements())
                .laboratory(CalculationLaboratory.builder()
                    .laboratoryName(m.getLaboratoryName())
                    .laboratoryAccredited(m.isLaboratoryAccredited())
                    .build())
                .reducedSamplingFrequencyJustification(m.isFrequencyMeetsMinRequirements() ? null : 
                    ReducedSamplingFrequencyJustification.builder()
                    .isCostUnreasonable(m.getReducedFrequencyReason().equals(UNREASONABLE_COSTS))
                    .isOneThirdRuleAndSampling(m.getReducedFrequencyReason().equals(ONE_THIRD_UNCERTAINTY))
                    .build())
                .build())
        .collect(Collectors.toList());
    }
    
    private NoHighestRequiredTierJustification getTierJustification(final boolean highestTier, final String justification) {
        
        return highestTier ? null :
            NoHighestRequiredTierJustification.builder()
                .isCostUnreasonable(Boolean.FALSE)
                .isTechnicallyInfeasible(Boolean.TRUE)
                .technicalInfeasibilityExplanation(justification)
                .build();
    }
    
    private void replaceReferencesWithIds(final Map<String, CalculationMonitoringApproach> sections,
                                          final Map<Long, PermitMigrationContainer> permits,
                                          final Map<String, Account> accountsToMigratePermit) {

        sections.forEach((id, approach) -> {
            
            final Long accountId = accountsToMigratePermit.get(id).getId();
            final Permit permit = permits.get(accountId).getPermitContainer().getPermit();
            final List<SourceStream> sourceStreams = permit.getSourceStreams().getSourceStreams();
            final List<EmissionSource> emissionSources = permit.getEmissionSources().getEmissionSources();
            final List<MeasurementDeviceOrMethod> measurementDevicesOrMethods = permit.getMeasurementDevicesOrMethods().getMeasurementDevicesOrMethods();

            approach.getSourceStreamCategoryAppliedTiers().forEach(tier -> {

                    // source stream
                    final String sourceStreamRef = tier.getSourceStreamCategory().getSourceStream();
                    final String sourceStreamId = sourceStreams.stream()
                        .filter(ss -> ss.getReference().trim().equalsIgnoreCase(sourceStreamRef.trim()))
                        .findAny()
                        .map(SourceStream::getId)
                        .orElseGet(() -> {
                            log.error(String.format("cannot find source stream with reference %s for emitter %s",
                                sourceStreamRef,
                                id));
                            return sourceStreamRef;
                        });
                    tier.getSourceStreamCategory().setSourceStream(sourceStreamId);

                    // emission sources
                    final Set<String> emissionSourcesIds =
                        tier.getSourceStreamCategory().getEmissionSources().stream().map(
                            emissionSourceRef -> emissionSources.stream()
                                .filter(es -> es.getReference().trim().equalsIgnoreCase(emissionSourceRef.trim()))
                                .findAny()
                                .map(EmissionSource::getId)
                                .orElseGet(() -> {
                                    log.error(String.format("cannot find emission source with reference %s for emitter %s",
                                            emissionSourceRef,
                                            id));
                                    return emissionSourceRef;
                                })
                        ).collect(Collectors.toSet());
                    tier.getSourceStreamCategory().setEmissionSources(emissionSourcesIds);

                    // measurement devices
                    final Set<String> deviceIds = tier.getActivityData().getMeasurementDevicesOrMethods().stream().map(
                        deviceRef -> measurementDevicesOrMethods.stream()
                            .filter(dev -> dev.getReference().trim().equalsIgnoreCase(deviceRef.trim()))
                            .findAny()
                            .map(MeasurementDeviceOrMethod::getId)
                            .orElseGet(() -> {
                                log.error(String.format("cannot find measurement device with reference %s for emitter %s",
                                        deviceRef,
                                        id));
                                return deviceRef;
                            })
                    ).collect(Collectors.toSet());
                    tier.getActivityData().setMeasurementDevicesOrMethods(deviceIds);
                }
            );
            }
        );
    }
}
