package uk.gov.pmrv.api.permit.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.common.SourceStreamCategoryType;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OEmissionType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.N2OSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.measuredemissions.N2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.n2o.measuredemissions.N2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

@ExtendWith(MockitoExtension.class)
class N2OMonitoringApproachSectionValidatorTest {

    @InjectMocks
    private N2OMonitoringApproachSectionValidator validator;
    
    @Mock
    private PermitReferenceService permitReferenceService;

    private static String sourceStreamId1, sourceStreamId2,
            emissionSourceId1, emissionSourceId2,
            emissionPointId1, emissionPointId2,
            measurementDeviceOrMethodId1, measurementDeviceOrMethodId2;
    private static SourceStream sourceStream1, sourceStream2;
    private static EmissionSource emissionSource1, emissionSource2;
    private static EmissionPoint emissionPoint1, emissionPoint2;
    private static MeasurementDeviceOrMethod measurementDeviceOrMethod1, measurementDeviceOrMethod2;
    private static UUID file1, file2;

    @BeforeAll
    static void setup() {
        sourceStreamId1 = UUID.randomUUID().toString();
        sourceStreamId2 = UUID.randomUUID().toString();
        emissionSourceId1 = UUID.randomUUID().toString();
        emissionSourceId2 = UUID.randomUUID().toString();
        emissionPointId1 = UUID.randomUUID().toString();
        emissionPointId2 = UUID.randomUUID().toString();
        measurementDeviceOrMethodId1 = UUID.randomUUID().toString();
        measurementDeviceOrMethodId2 = UUID.randomUUID().toString();
        file1 = UUID.randomUUID();
        file2 = UUID.randomUUID();
        
        sourceStream1 = SourceStream.builder().id(sourceStreamId1).reference("sourceStream1").build();
        sourceStream2 = SourceStream.builder().id(sourceStreamId2).reference("sourceStream2").build();
        emissionSource1 = EmissionSource.builder().id(emissionSourceId1).reference("emissionSource1").build();
        emissionSource2 = EmissionSource.builder().id(emissionSourceId2).reference("emissionSource2").build();
        emissionPoint1 = EmissionPoint.builder().id(emissionPointId1).reference("emissionPoint1").build();
        emissionPoint2 = EmissionPoint.builder().id(emissionPointId2).reference("emissionPoint2").build();
        measurementDeviceOrMethod1 = MeasurementDeviceOrMethod.builder().id(measurementDeviceOrMethodId1).build();
        measurementDeviceOrMethod2 = MeasurementDeviceOrMethod.builder().id(measurementDeviceOrMethodId2).build();
    }

    @Test
    void validatePermitContainer() {
        final N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(
                sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                N2OMeasuredEmissions.builder()
                        .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                        .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                        .tier(N2OMeasuredEmissionsTier.TIER_3)
                        .highestRequiredTier(HighestRequiredTier.builder().build())
                        .build());
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                    .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(Map.of(MonitoringApproachType.N2O, monitoringApproach))
                        .build())
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validatePermitContainer_no_N2O() {
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                    .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()))
                        .build())
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validateSection() {
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                    .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2), 
                Set.of(emissionPointId1, emissionPointId2),
                N2OMeasuredEmissions.builder()
                    .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                    .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                    .tier(N2OMeasuredEmissionsTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .build());

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_source_stream() {
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                    .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(sourceStreamId2,
                Set.of(emissionSourceId1, emissionSourceId2), 
                Set.of(emissionPointId1, emissionPointId2),
                N2OMeasuredEmissions.builder()
                    .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                    .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                    .tier(N2OMeasuredEmissionsTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .build());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                        List.of(sourceStream2.getId()))));

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(N2OMonitoringApproach.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                        List.of(sourceStream2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_emission_source() {
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                    .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2), 
                Set.of(emissionPointId1, emissionPointId2),
                N2OMeasuredEmissions.builder()
                    .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                    .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                    .tier(N2OMeasuredEmissionsTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .build());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                        List.of(emissionSource2.getId()))));

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(N2OMonitoringApproach.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                        List.of(emissionSource2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_emission_point() {
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                    .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2), 
                Set.of(emissionPointId1, emissionPointId2),
                N2OMeasuredEmissions.builder()
                    .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                    .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                    .tier(N2OMeasuredEmissionsTier.TIER_3)
                    .highestRequiredTier(HighestRequiredTier.builder().build())
                    .build());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                        List.of(emissionPoint2.getId()))));

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(N2OMonitoringApproach.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                        List.of(emissionPoint2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_null_section() {
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                    .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        PermitValidationResult result = validator.validate(null, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validateMeasuredEmissionsMeasurementDevicesOrMethodsExist_whenSomeNotExist_thenPermitViolation() {
        final PermitContainer permitContainer = buildPermitContainer();
        
        final N2OMeasuredEmissions n2OMeasuredEmissions = N2OMeasuredEmissions.builder()
            .tier(N2OMeasuredEmissionsTier.TIER_3)
            .highestRequiredTier(HighestRequiredTier.builder().build())
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
            .measurementDevicesOrMethods(Set.of("1", measurementDeviceOrMethodId1))
            .build();

        final N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(sourceStreamId1,
            Set.of(emissionSourceId1, emissionSourceId2),
            Set.of(emissionPointId1, emissionPointId2),
            n2OMeasuredEmissions);

        when(permitReferenceService.validateExistenceInPermit(
                permitContainer.getPermit().getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permitContainer.getPermit().getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permitContainer.getPermit().getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                        List.of("1"))));
        
        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(new PermitViolation(N2OMonitoringApproach.class.getSimpleName(),
            PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
            List.of("1").toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateMeasuredEmissionsMeasurementDevicesOrMethodsExist_whenAllExist_thenAllow() {
        final PermitContainer permitContainer = buildPermitContainer();
        
        final N2OMeasuredEmissions n2OMeasuredEmissions = N2OMeasuredEmissions.builder()
            .tier(N2OMeasuredEmissionsTier.TIER_3)
            .highestRequiredTier(HighestRequiredTier.builder().build())
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
            .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1, measurementDeviceOrMethodId2))
            .build();
        
        final N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(sourceStreamId1,
            Set.of(emissionSourceId1, emissionSourceId2),
            Set.of(emissionPointId1, emissionPointId2),
            n2OMeasuredEmissions);

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateMeasuredEmissionsFilesExist_whenAllExist_thenAllow() {
        final PermitContainer permitContainer = buildPermitContainer();
        
        final N2OMeasuredEmissions n2OMeasuredEmissions = N2OMeasuredEmissions.builder()
            .tier(N2OMeasuredEmissionsTier.TIER_1)
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
            .highestRequiredTier(HighestRequiredTier.builder()
                    .isHighestRequiredTier(Boolean.FALSE)
                    .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                            .isCostUnreasonable(Boolean.TRUE)
                            .files(Set.of(file1, file2))
                            .build())
                    .build())
            .build();
        
        final N2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(sourceStreamId1,
            Set.of(emissionSourceId1, emissionSourceId2),
            Set.of(emissionPointId1, emissionPointId2),
            n2OMeasuredEmissions);

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getEmissionSourcesIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getEmissionPointsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }
    
    private N2OMonitoringApproach buildN2OMonitoringApproach(String sourceStream, 
                                                             Set<String> emissionSources, 
                                                             Set<String> emissionPoints,
                                                             N2OMeasuredEmissions measuredEmissions) {
        return N2OMonitoringApproach.builder()
                .sourceStreamCategoryAppliedTiers(List.of(N2OSourceStreamCategoryAppliedTier.builder()
                        .sourceStreamCategory(N2OSourceStreamCategory.builder()
                                .sourceStream(sourceStream)
                                .emissionSources(emissionSources)
                                .emissionPoints(emissionPoints)
                                .emissionType(N2OEmissionType.ABATED)
                                .monitoringApproachType(N2OMonitoringApproachType.CALCULATION)
                                .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                                .categoryType(SourceStreamCategoryType.MAJOR)
                                .build())
                        .measuredEmissions(measuredEmissions)
                        .build()))
                .build();
    }

    private PermitContainer buildPermitContainer() {

        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(
                EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();

        return PermitContainer.builder()
            .permit(permit)
            .permitAttachments(Map.of(file1, "file1", file2, "file2"))
            .build();
    }
}
