package uk.gov.pmrv.api.permit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.SpringValidatorConfiguration;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.common.MeasurementDeviceType;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.common.SourceStreamCategoryType;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.domain.installationdesc.InstallationDescription;
import uk.gov.pmrv.api.permit.domain.managementprocedures.DataFlowActivities;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProceduresDefinition;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.*;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CalculationAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Laboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeteringUncertainty;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OEmissionType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.measuredemissions.N2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.measuredemissions.N2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.ActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.CellAndAnodeType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCCalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCTier2EmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.ReceivingTransferringInstallation;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.ReceivingTransferringInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TemperaturePressure;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions.AccountingEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringReporting;
import uk.gov.pmrv.api.permit.domain.monitoringreporting.MonitoringRole;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sitediagram.SiteDiagrams;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.permit.domain.uncertaintyanalysis.UncertaintyAnalysis;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(value = {ObjectMapper.class, SpringValidatorConfiguration.class})
@Sql(statements = {
        "INSERT INTO ref_country (id, code, name, official_name) VALUES (1, 'GR', 'Greece', 'The Hellenic Republic')"
})
class PermitRepositoryIT extends AbstractContainerBaseTest {

    private static final String SAMPLE_PERMIT_ID = "UK-1";
    private static final String SAMPLE_PERMIT_ID_2 = "UK-2";

    @Autowired
    private PermitRepository repo;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private Validator validator;

    @Test
    void validate_invalid_permit_should_return_violations() {
        PermitEntity permitEntity = PermitEntity.builder()
            .accountId(1L)
            .permitContainer(PermitContainer.builder()
                .permit(Permit.builder().build())
                .installationOperatorDetails(buildInstallationOperatorDetails())
                .build())
            .build();

        Set<ConstraintViolation<PermitEntity>> violations = validator.validate(permitEntity);
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream().allMatch(v -> v.getPropertyPath().toString().contains("permit"))).isTrue();
    }

    @Test
    void validate_valid_permit_should_return_no_violations() {
        PermitEntity permitEntity = PermitEntity.builder()
            .accountId(1L)
            .permitContainer(buildFullPermit())
            .build();

        Set<ConstraintViolation<PermitEntity>> violations = validator.validate(permitEntity);
        assertThat(violations).isEmpty();
    }

    @Test
    void saveAndQuery() {
        Long accountId = 1L;

        PermitContainer permit = buildFullPermit();

        PermitEntity permitEntity = PermitEntity.builder()
            .id(SAMPLE_PERMIT_ID)
            .accountId(accountId)
            .permitContainer(permit)
            .build();

        validator.validate(permitEntity);

        repo.save(permitEntity);

        entityManager.flush();
        entityManager.clear();

        PermitEntity permitEntityFound = repo.getById(permitEntity.getId());

        assertThat(permitEntityFound.getPermitContainer()).isEqualTo(permit);
        assertThat(permitEntityFound).isNotNull();
    }

    @Test
    void findPermitEntityAccountByAttachmentUuid() {
        Long accountId1 = 1L;
        PermitContainer permit1 = buildFullPermit();
        PermitEntity permitEntity1 = PermitEntity.builder()
            .id(SAMPLE_PERMIT_ID)
            .accountId(accountId1)
            .permitContainer(permit1)
            .build();
        repo.save(permitEntity1);

        Long accountId2 = 2L;
        PermitContainer permit2 = buildFullPermit();
        PermitEntity permitEntity2 = PermitEntity.builder()
            .id(SAMPLE_PERMIT_ID_2)
            .accountId(accountId2)
            .permitContainer(permit2)
            .build();
        repo.save(permitEntity2);

        UUID siteDiagram1 =
            permitEntity1.getPermitContainer().getPermit().getSiteDiagrams().getSiteDiagrams().iterator().next();
        entityManager.flush();
        entityManager.clear();

        Optional<PermitEntityAccountDTO> result1 =
            repo.findPermitEntityAccountByAttachmentUuid(siteDiagram1.toString());
        assertThat(result1).isNotEmpty();
        assertThat(result1.get().getAccountId()).isEqualTo(accountId1);
        assertThat(result1.get().getPermitEntityId()).isEqualTo(permitEntity1.getId());
    }

    @Test
    void findPermitEntityAccountByAttachmentUuid_empty_result() {
        Optional<PermitEntityAccountDTO> result =
            repo.findPermitEntityAccountByAttachmentUuid(UUID.randomUUID().toString());
        assertThat(result).isEmpty();
    }

    private PermitContainer buildFullPermit() {
        PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.GHGE)
            .permit(Permit.builder()
                .additionalDocuments(buildAdditionalDocuments())
                .environmentalPermitsAndLicences(buildEnvPermitsAndLicences())
                .estimatedAnnualEmissions(buildEstimatedAnnualEmissions())
                .installationDescription(buildInstallationDescription())
                .regulatedActivities(buildRegulatedActivities())
                .monitoringMethodologyPlans(buildMonitoringMethodologyPlan())
                .measurementDevicesOrMethods(buildMeasurementDevicesOrMethods())
                .sourceStreams(buildSourceStreams())
                .emissionSources(buildEmissionSources())
                .emissionPoints(buildEmissionPoints())
                .emissionSummaries(buildEmissionSummaries())
                .siteDiagrams(buildSiteDiagrams())
                .abbreviations(buildAbbreviations())
                .confidentialityStatement(buildConfidentialityStatement())
                .monitoringApproaches(buildMonitoringApproaches())
                .uncertaintyAnalysis(UncertaintyAnalysis.builder().exist(false).attachments(Set.of()).build())
                .managementProcedures(buildManagementProcedures())
                .build())
            .installationOperatorDetails(buildInstallationOperatorDetails())
            .build();
        permitContainer.setPermitAttachments(
            permitContainer.getPermit().getPermitSectionAttachmentIds()
                .stream()
                .collect(Collectors.toMap(Function.identity(), UUID::toString)));
        return permitContainer;
    }

    private RegulatedActivities buildRegulatedActivities() {
        return RegulatedActivities.builder()
            .regulatedActivities(List.of(
                RegulatedActivity.builder()
                    .id(UUID.randomUUID().toString())
                    .type(RegulatedActivityType.COMBUSTION)
                    .capacity(BigDecimal.valueOf(1L))
                    .capacityUnit(CapacityUnit.TONNES_PER_DAY).build()))
            .build();
    }

    private EstimatedAnnualEmissions buildEstimatedAnnualEmissions() {
        return EstimatedAnnualEmissions.builder()
            .quantity(BigDecimal.valueOf(25000.1))
            .build();
    }

    private EnvironmentalPermitsAndLicences buildEnvPermitsAndLicences() {
        return EnvironmentalPermitsAndLicences.builder()
            .exist(false)
            .build();
    }

    private InstallationOperatorDetails buildInstallationOperatorDetails() {
        return InstallationOperatorDetails.builder()
                .installationName("installationName")
                .siteName("siteName")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("ST330000")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("GR")
                                .postcode("postcode")
                                .build())
                        .build())
                .operator("operator")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("408812")
                .operatorDetailsAddress(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .postcode("postcode")
                        .build())
                .build();
    }

    private InstallationDescription buildInstallationDescription() {
        return InstallationDescription.builder()
            .mainActivitiesDesc("mainActivitiesDesc")
            .siteDescription("siteDescription")
            .build();
    }

    private ManagementProcedures buildManagementProcedures() {
        ManagementProceduresDefinition managementProceduresDefinition = buildManagementAndProceduresSection();
        return ManagementProcedures.builder()
            .managementProceduresExist(true)
            .monitoringReporting(buildMonitoringReporting())
            .dataFlowActivities(buildDataFlowActivitiesSection())
            .assessAndControlRisk(managementProceduresDefinition)
            .assignmentOfResponsibilities(managementProceduresDefinition)
            .controlOfOutsourcedActivities(managementProceduresDefinition)
            .correctionsAndCorrectiveActions(managementProceduresDefinition)
            .monitoringPlanAppropriateness(managementProceduresDefinition)
            .qaDataFlowActivities(managementProceduresDefinition)
            .qaMeteringAndMeasuringEquipment(managementProceduresDefinition)
            .recordKeepingAndDocumentation(managementProceduresDefinition)
            .reviewAndValidationOfData(managementProceduresDefinition)
            .environmentalManagementSystem(buildEnvironmentalManagementSystem())
            .build();
    }

    private ManagementProceduresDefinition buildManagementAndProceduresSection() {
        return ManagementProceduresDefinition.builder()
            .procedureDocumentName("procDocName")
            .procedureReference("procRef")
            .diagramReference("diagramRef")
            .procedureDescription("procDesc")
            .responsibleDepartmentOrRole("dep")
            .locationOfRecords("loc")
            .itSystemUsed("system")
            .appliedStandards("standards")
            .build();
    }

    private DataFlowActivities buildDataFlowActivitiesSection() {
        return DataFlowActivities.builder()
            .procedureDocumentName("procDocName")
            .procedureReference("procRef")
            .diagramReference("diagramRef")
            .procedureDescription("procDesc")
            .responsibleDepartmentOrRole("dep")
            .locationOfRecords("loc")
            .itSystemUsed("system")
            .appliedStandards("standards")
            .primaryDataSources("primaryDs")
            .processingSteps("steps")
            .build();
    }

    private MonitoringReporting buildMonitoringReporting() {
        return MonitoringReporting.builder()
            .monitoringRoles(List.of(MonitoringRole.builder().jobTitle("jobTitle").mainDuties("mainDuties").build()))
            .organisationCharts(Set.of(UUID.randomUUID()))
            .build();
    }

    private EnvironmentalManagementSystem buildEnvironmentalManagementSystem() {
        return EnvironmentalManagementSystem.builder()
            .exist(true)
            .certified(true)
            .certificationStandard("ISO-3476")
            .build();
    }

    private MonitoringMethodologyPlans buildMonitoringMethodologyPlan() {
        return MonitoringMethodologyPlans.builder()
            .exist(true)
            .plans(Set.of(UUID.randomUUID()))
            .build();
    }

    private MeasurementDevicesOrMethods buildMeasurementDevicesOrMethods() {
        return MeasurementDevicesOrMethods.builder()
            .measurementDevicesOrMethods(List.of(MeasurementDeviceOrMethod
                .builder()
                .id(UUID.randomUUID().toString())
                .reference("reference")
                .type(MeasurementDeviceType.LEVEL_GAUGE)
                .measurementRange("range")
                .meteringRangeUnits("units")
                .location("location")
                .build()))
            .build();
    }

    private SourceStreams buildSourceStreams() {
        return SourceStreams.builder()
            .sourceStreams(List.of(SourceStream
                .builder()
                .id(UUID.randomUUID().toString())
                .reference("ref")
                .type(SourceStreamType.AMMONIA_FUEL_AS_PROCESS_INPUT)
                .description(SourceStreamDescription.BIODIESELS)
                .build()))
            .build();
    }

    private EmissionSources buildEmissionSources() {
        return EmissionSources.builder()
            .emissionSources(List.of(
                EmissionSource.builder()
                    .id(UUID.randomUUID().toString())
                    .reference("reference")
                    .description("description")
                    .build()))
            .build();
    }

    private EmissionPoints buildEmissionPoints() {
        return EmissionPoints.builder()
            .emissionPoints(List.of(EmissionPoint.builder()
                .id(UUID.randomUUID().toString())
                .reference("reference")
                .description("description")
                .build()))
            .build();
    }

    private EmissionSummaries buildEmissionSummaries() {
        return EmissionSummaries.builder()
            .emissionSummaries(List.of(EmissionSummary.builder()
                .sourceStream(UUID.randomUUID().toString())
                .emissionSources(Set.of(UUID.randomUUID().toString()))
                .emissionPoints(Set.of(UUID.randomUUID().toString()))
                .regulatedActivity(UUID.randomUUID().toString())
                .excludedRegulatedActivity(false)
                .build()))
            .build();
    }

    private SiteDiagrams buildSiteDiagrams() {
        return SiteDiagrams.builder()
            .siteDiagrams(Set.of(UUID.randomUUID()))
            .build();
    }

    private Abbreviations buildAbbreviations() {
        return Abbreviations.builder()
            .exist(false)
            .build();
    }

    private ConfidentialityStatement buildConfidentialityStatement() {
        return ConfidentialityStatement.builder().exist(false).build();
    }

    private AdditionalDocuments buildAdditionalDocuments() {
        return AdditionalDocuments.builder().exist(false).build();
    }

    private MonitoringApproaches buildMonitoringApproaches() {
        EnumMap<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            new EnumMap<>(MonitoringApproachType.class);

        monitoringApproaches.put(MonitoringApproachType.CALCULATION, buildCalculationMonitorApproach());
        monitoringApproaches.put(MonitoringApproachType.INHERENT_CO2, buildInherentCO2MonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.TRANSFERRED_CO2, buildTransferredCO2MonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.FALLBACK, buildFallbackMonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.N2O, buildN2OMonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.MEASUREMENT, buildMeasurementMonitoringApproach());
        monitoringApproaches.put(MonitoringApproachType.PFC, buildPFCMonitoringApproach());

        return MonitoringApproaches.builder().monitoringApproaches(monitoringApproaches).build();
    }

    private PFCMonitoringApproach buildPFCMonitoringApproach() {
        return PFCMonitoringApproach.builder()
            .type(MonitoringApproachType.PFC)
            .approachDescription("pfc approach description")
            .cellAndAnodeTypes(List.of(
                CellAndAnodeType.builder()
                    .cellType("cell type")
                    .anodeType("anode type")
                    .build()
            ))
            .tier2EmissionFactor(PFCTier2EmissionFactor.builder().exist(false).build())
            .collectionEfficiency(buildProcedureForm("collectionEfficiency"))
            .sourceStreamCategoryAppliedTiers(List.of(PFCSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(PFCSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(2600.12))
                    .categoryType(SourceStreamCategoryType.MAJOR)
                    .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                    .build())
                .activityData(PFCActivityData.builder()
                    .massBalanceApproachUsed(true)
                    .tier(ActivityDataTier.TIER_4)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .build())
                .emissionFactor(PFCEmissionFactor.builder()
                    .tier(PFCEmissionFactorTier.TIER_1)
                    .highestRequiredTier(HighestRequiredTier.builder()
                        .isHighestRequiredTier(Boolean.FALSE)
                        .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                            .isTechnicallyInfeasible(Boolean.TRUE)
                            .technicalInfeasibilityExplanation("explain")
                            .isCostUnreasonable(Boolean.TRUE)
                            .build())
                        .build())
                    .build())
                .build()))
            .build();
    }

    private MeasMonitoringApproach buildMeasurementMonitoringApproach() {
        return MeasMonitoringApproach.builder()
            .type(MonitoringApproachType.MEASUREMENT)
            .approachDescription("measurementApproachDescription")
            .emissionDetermination(buildProcedureForm("emissionDetermination"))
            .referencePeriodDetermination(buildProcedureForm("referencePeriodDetermination"))
            .gasFlowCalculation(ProcedureOptionalForm.builder().exist(false).build())
            .biomassEmissions(ProcedureOptionalForm.builder().exist(false).build())
            .corroboratingCalculations(buildProcedureForm("corroboratingCalculations"))
            .sourceStreamCategoryAppliedTiers(List.of(MeasSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(MeasSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.123))
                    .categoryType(SourceStreamCategoryType.MAJOR).build())
                .measuredEmissions(buildMeasMeasuredEmissions())
                .appliedStandard(buildAppliedStandard())
                .build()))
            .build();
    }

    private N2OMonitoringApproach buildN2OMonitoringApproach() {
        return N2OMonitoringApproach.builder()
            .type(MonitoringApproachType.N2O)
            .approachDescription("approachDescription")
            .emissionDetermination(buildProcedureForm("emissionDetermination"))
            .referenceDetermination(buildProcedureForm("determinationReference"))
            .operationalManagement(buildProcedureForm("operationalManagement"))
            .nitrousOxideEmissionsDetermination(buildProcedureForm("determinationNitrousOxideEmissions"))
            .nitrousOxideConcentrationDetermination(buildProcedureForm("determinationNitrousOxideConcentration"))
            .quantityProductDetermination(buildProcedureForm("determinationQuantityProduct"))
            .quantityMaterials(buildProcedureForm("quantityMaterials"))
            .gasFlowCalculation(ProcedureOptionalForm.builder().exist(false).build())
            .sourceStreamCategoryAppliedTiers(List.of(N2OSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(N2OSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .emissionPoints(Set.of(UUID.randomUUID().toString()))
                    .emissionType(N2OEmissionType.ABATED)
                    .monitoringApproachType(N2OMonitoringApproachType.CALCULATION)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                    .categoryType(SourceStreamCategoryType.MAJOR)
                    .build())
                .measuredEmissions(buildN2OMeasuredEmissions())
                .appliedStandard(buildAppliedStandard())
                .build()))
            .build();
    }

    private FallbackMonitoringApproach buildFallbackMonitoringApproach() {
        return FallbackMonitoringApproach.builder()
            .type(MonitoringApproachType.FALLBACK)
            .approachDescription("fallbackApproachDescription")
            .justification("fallbackApproachJustification")
            .annualUncertaintyAnalysis(buildProcedureForm("annualUncertaintyAnalysis"))
            .sourceStreamCategoryAppliedTiers(List.of(FallbackSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(FallbackSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                    .categoryType(SourceStreamCategoryType.MAJOR)
                    .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
                    .uncertainty(MeteringUncertainty.LESS_OR_EQUAL_2_5)
                    .build())
                .build()))
            .build();
    }

    private TransferredCO2MonitoringApproach buildTransferredCO2MonitoringApproach() {
        return TransferredCO2MonitoringApproach.builder().type(MonitoringApproachType.TRANSFERRED_CO2)
            .receivingTransferringInstallations(List.of(
                ReceivingTransferringInstallation.builder()
                    .type(ReceivingTransferringInstallationType.RECEIVING)
                    .installationIdentificationCode("code1")
                    .installationName("name1")
                    .operator("operator1")
                    .co2source("source1").build()
            ))
            .accountingEmissions(AccountingEmissions.builder()
                .chemicallyBound(true)
                .build())
            .deductionsToAmountOfTransferredCO2(ProcedureOptionalForm.builder()
                .exist(false)
                .build())
            .procedureForLeakageEvents(ProcedureOptionalForm.builder()
                .exist(false)
                .build())
            .temperaturePressure(TemperaturePressure.builder()
                .exist(false)
                .build())
            .transferOfCO2(buildProcedureForm("transferOfCO2"))
            .quantificationMethodologies(buildProcedureForm("quantificationMethodologies"))
            .approachDescription("approachDescription").build();
    }

    private InherentCO2MonitoringApproach buildInherentCO2MonitoringApproach() {
        return InherentCO2MonitoringApproach.builder().type(MonitoringApproachType.INHERENT_CO2)
            .approachDescription("description").build();
    }

    private CalculationMonitoringApproach buildCalculationMonitorApproach() {
        return CalculationMonitoringApproach.builder().type(MonitoringApproachType.CALCULATION)
            .samplingPlan(SamplingPlan.builder().exist(true)
                .details(SamplingPlanDetails.builder()
                    .analysis(buildProcedureForm("analysis"))
                    .procedurePlan(buildProcedurePlan("samplingplanprocedure"))
                    .appropriateness(buildProcedureForm("appropriateness"))
                    .yearEndReconciliation(ProcedureOptionalForm.builder().exist(false).build())
                    .build())
                .build())
            .approachDescription("description")
            .sourceStreamCategoryAppliedTiers(List.of(CalculationSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                    .sourceStream(UUID.randomUUID().toString())
                    .emissionSources(Set.of(UUID.randomUUID().toString()))
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                    .categoryType(SourceStreamCategoryType.MAJOR)
                    .calculationMethod(CalculationMethod.MASS_BALANCE)
                    .build())
                .emissionFactor(CalculationEmissionFactor.builder()
                    .exist(true)
                    .tier(CalculationEmissionFactorTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .oneThirdRule(Boolean.FALSE)
                    .defaultValueApplied(Boolean.TRUE)
                    .standardReferenceSource(CalculationEmissionFactorStandardReferenceSource.builder().type(CalculationEmissionFactorStandardReferenceSourceType.IN_HOUSE_CALCULATION).build())
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(Boolean.TRUE)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("dfdfd")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build())
                    .build())
                .oxidationFactor(CalculationOxidationFactor.builder()
                    .exist(true)
                    .tier(CalculationOxidationFactorTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("dfdfd")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build())
                    .build())
                .carbonContent(CalculationCarbonContent.builder()
                    .exist(true)
                    .tier(CalculationCarbonContentTier.TIER_2A)
                    .highestRequiredTier(HighestRequiredTier.builder().isHighestRequiredTier(Boolean.TRUE).build())
                    .defaultValueApplied(Boolean.FALSE)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(Boolean.TRUE)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("Carbon content analysis method desc")
                            .samplingFrequency(CalculationSamplingFrequency.DAILY)
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isOneThirdRuleAndSampling(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .conversionFactor(CalculationConversionFactor.builder()
                    .exist(true)
                    .tier(CalculationConversionFactorTier.TIER_2)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("Conversion factor analysis method desc")
                            .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS)
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .activityData(CalculationActivityData.builder()
                    .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
                    .uncertainty(MeteringUncertainty.LESS_OR_EQUAL_5_0)
                    .tier(CalculationActivityDataTier.TIER_1)
                    .highestRequiredTier(HighestRequiredTier.builder()
                        .isHighestRequiredTier(Boolean.FALSE)
                        .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                            .isTechnicallyInfeasible(Boolean.TRUE)
                            .technicalInfeasibilityExplanation("explain")
                            .isCostUnreasonable(Boolean.TRUE)
                            .build())
                        .build())
                    .build())
                .netCalorificValue(CalculationNetCalorificValue.builder()
                    .exist(true)
                    .tier(CalculationNetCalorificValueTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                        .analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("dfdfd")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .biomassFraction(CalculationBiomassFraction.builder()
                    .exist(true)
                    .tier(CalculationBiomassFractionTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .defaultValueApplied(false)
                    .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(true)
                        .analysisMethods(List.of(CalculationAnalysisMethod.builder()
                            .analysis("analysis")
                            .samplingFrequency(CalculationSamplingFrequency.OTHER)
                            .samplingFrequencyOtherDetails("other")
                            .frequencyMeetsMinRequirements(false)
                            .laboratory(
                                CalculationLaboratory.builder().laboratoryName("fdfdfd").laboratoryAccredited(true)
                                    .build())
                            .reducedSamplingFrequencyJustification(ReducedSamplingFrequencyJustification.builder()
                                .isCostUnreasonable(Boolean.TRUE)
                                .build())
                            .build()))
                        .build()).build())
                .build()))
            .build();
    }

    private ProcedureForm buildProcedureForm(String value) {
        return ProcedureForm.builder()
            .procedureDescription("procedureDescription" + value)
            .procedureDocumentName("procedureDocumentName" + value)
            .procedureReference("procedureReference" + value)
            .diagramReference("diagramReference" + value)
            .responsibleDepartmentOrRole("responsibleDepartmentOrRole" + value)
            .locationOfRecords("locationOfRecords" + value)
            .itSystemUsed("itSystemUsed" + value)
            .appliedStandards("appliedStandards" + value)
            .build();
    }

    private N2OMeasuredEmissions buildN2OMeasuredEmissions() {
        return N2OMeasuredEmissions.builder()
            .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
            .tier(N2OMeasuredEmissionsTier.TIER_2)
            .highestRequiredTier(HighestRequiredTier.builder()
                .isHighestRequiredTier(Boolean.FALSE)
                .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                    .isCostUnreasonable(Boolean.TRUE)
                    .files(Set.of(UUID.randomUUID()))
                    .build())
                .build())
            .build();
    }

    private MeasMeasuredEmissions buildMeasMeasuredEmissions() {
        return MeasMeasuredEmissions.builder()
            .measurementDevicesOrMethods(Set.of(UUID.randomUUID().toString()))
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
            .tier(MeasMeasuredEmissionsTier.TIER_4)
            .highestRequiredTier(HighestRequiredTier.builder().build())
            .build();
    }

    private ProcedurePlan buildProcedurePlan(String value) {
        return ProcedurePlan.builder()
            .procedureDescription("procedureDescription" + value)
            .procedureDocumentName("procedureDocumentName" + value)
            .procedureReference("procedureReference" + value)
            .diagramReference("diagramReference" + value)
            .responsibleDepartmentOrRole("responsibleDepartmentOrRole" + value)
            .locationOfRecords("locationOfRecords" + value)
            .itSystemUsed("itSystemUsed" + value)
            .appliedStandards("appliedStandards" + value)
            .procedurePlanIds(Set.of(UUID.randomUUID(), UUID.randomUUID()))
            .build();
    }

    private AppliedStandard buildAppliedStandard() {
        return AppliedStandard.builder()
            .parameter("parameter")
            .appliedStandard("applied standard")
            .deviationFromAppliedStandardExist(true)
            .deviationFromAppliedStandardDetails("deviation details")
            .laboratory(Laboratory.builder().laboratoryName("lab").laboratoryAccredited(true).build())
            .build();
    }
}
