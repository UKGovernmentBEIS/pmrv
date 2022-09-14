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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.MeasSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurement.measuredemissions.MeasMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

@ExtendWith(MockitoExtension.class)
class MeasMonitoringApproachSectionValidatorTest {

    @InjectMocks
    private MeasMonitoringApproachSectionValidator validator;

    @Mock
    private PermitReferenceService permitReferenceService;

    private static String sourceStreamId1, sourceStreamId2,
            emissionSourceId1, emissionSourceId2,
            emissionPointId1, emissionPointId2,
            deviceId1, deviceId2;
    private static SourceStream sourceStream1, sourceStream2;
    private static EmissionSource emissionSource1, emissionSource2;
    private static EmissionPoint emissionPoint1, emissionPoint2;
    private static MeasurementDeviceOrMethod device1, device2;
    private static UUID file1;

    @BeforeAll
    static void setup() {

        sourceStreamId1 = UUID.randomUUID().toString();
        sourceStreamId2 = UUID.randomUUID().toString();
        emissionSourceId1 = UUID.randomUUID().toString();
        emissionSourceId2 = UUID.randomUUID().toString();
        emissionPointId1 = UUID.randomUUID().toString();
        emissionPointId2 = UUID.randomUUID().toString();
        deviceId1 = UUID.randomUUID().toString();
        deviceId2 = UUID.randomUUID().toString();
        file1 = UUID.randomUUID();

        sourceStream1 = SourceStream.builder().id(sourceStreamId1).reference("sourceStream1").build();
        sourceStream2 = SourceStream.builder().id(sourceStreamId2).reference("sourceStream2").build();
        emissionSource1 = EmissionSource.builder().id(emissionSourceId1).reference("emissionSource1").build();
        emissionSource2 = EmissionSource.builder().id(emissionSourceId2).reference("emissionSource2").build();
        emissionPoint1 = EmissionPoint.builder().id(emissionPointId1).reference("emissionPoint1").build();
        emissionPoint2 = EmissionPoint.builder().id(emissionPointId2).reference("emissionPoint2").build();
        device1 = MeasurementDeviceOrMethod.builder().id(deviceId1).reference("deviceId1").build();
        device2 = MeasurementDeviceOrMethod.builder().id(deviceId2).reference("deviceId2").build();
    }

    @Test
    void validatePermitContainer_whenEverythingExists_thenAllow() {
        final MeasMonitoringApproach monitoringApproach = buildMeasurementMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                buildMeasuredEmissions(Set.of(deviceId1, deviceId2)));
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(
                        EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1, device2)).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(Map.of(MonitoringApproachType.MEASUREMENT, monitoringApproach))
                        .build())
                .build();

        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(permitContainer);

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
    void validatePermitContainer_whenNoMeasurementApproachExists_thenAllow() {
        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(
                EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(
                    Map.of(MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()))
                .build())
            .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validateSection_whenEverythingExists_thenAllow() {
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(
                    EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1, device2)).build())
                .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final MeasMonitoringApproach monitoringApproach = buildMeasurementMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                buildMeasuredEmissions(Set.of(deviceId1, deviceId2)));

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);

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
    void validateSection_whenInvalidSourceStream_thenPermitViolation() {
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(
                        EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1, device2)).build())
                .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final MeasMonitoringApproach monitoringApproach = buildMeasurementMonitoringApproach(sourceStreamId2,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                buildMeasuredEmissions(Set.of(deviceId1, deviceId2)));

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                        List.of(sourceStream2.getId()))));

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(MeasMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
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
    void validateSection_whenInvalidEmissionSource_thenPermitViolation() {
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1, device2)).build())
                .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final MeasMonitoringApproach monitoringApproach = buildMeasurementMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                buildMeasuredEmissions(Set.of(deviceId1, deviceId2)));

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

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(MeasMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
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
    void validateSection_whenInvalidEmissionPoint_thenPermitViolation() {
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(
                        EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1, device2)).build())
                .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final MeasMonitoringApproach monitoringApproach = buildMeasurementMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                buildMeasuredEmissions(Set.of(deviceId1, deviceId2)));

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

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(MeasMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
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
    void validateMeasuredEmissionsMeasurementDevicesOrMethodsExist_whenSomeNotExist_thenPermitViolation() {
        final MeasMeasuredEmissions measuredEmissions = MeasMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(deviceId1, deviceId2))
                .tier(MeasMeasuredEmissionsTier.TIER_3)
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                .highestRequiredTier(HighestRequiredTier.builder()
                        .isHighestRequiredTier(Boolean.TRUE)
                        .build())
                .build();

        final MeasMonitoringApproach monitoringApproach = buildMeasurementMonitoringApproach(
                sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                measuredEmissions);

        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(
                        EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1)).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(Map.of(MonitoringApproachType.MEASUREMENT, monitoringApproach))
                        .build())
                .build();

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

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
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                        List.of(deviceId2))));

        final PermitValidationResult result = validator.validate(permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(new PermitViolation(MeasMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                List.of(deviceId2).toArray()));
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
    void validateMeasuredEmissionsFilesExist_whenAllExist_thenAllow() {
        final MeasMeasuredEmissions measuredEmissions = MeasMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(deviceId1))
                .tier(MeasMeasuredEmissionsTier.TIER_3)
                .highestRequiredTier(HighestRequiredTier.builder()
                        .isHighestRequiredTier(Boolean.FALSE)
                        .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                                .isTechnicallyInfeasible(Boolean.TRUE)
                                .files(Set.of(file1))
                                .build())
                        .build())
                .build();

        final MeasMonitoringApproach monitoringApproach = buildMeasurementMonitoringApproach(
                sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(emissionPointId1, emissionPointId2),
                measuredEmissions);

        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(
                        EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1)).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(Map.of(MonitoringApproachType.MEASUREMENT, monitoringApproach))
                        .build())
                .build();

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .permitAttachments(Map.of(file1, "file1"))
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);

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
    void validateSection_whenSectionIsNull_thenAllow() {

        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(
                EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(null, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    private MeasMonitoringApproach buildMeasurementMonitoringApproach(final String sourceStream,
                                                                      final Set<String> emissionSources,
                                                                      final Set<String> emissionPoints,
                                                                      final MeasMeasuredEmissions measuredEmissions) {
        return MeasMonitoringApproach.builder()
            .sourceStreamCategoryAppliedTiers(List.of(MeasSourceStreamCategoryAppliedTier.builder()
                    .sourceStreamCategory(MeasSourceStreamCategory.builder()
                            .sourceStream(sourceStream)
                        .emissionSources(emissionSources)
                        .emissionPoints(emissionPoints)
                        .annualEmittedCO2Tonnes(BigDecimal.valueOf(25001.15))
                        .categoryType(SourceStreamCategoryType.MAJOR)
                        .build())
                    .measuredEmissions(measuredEmissions)
                .build()))
            .build();
    }

    private MeasMeasuredEmissions buildMeasuredEmissions(final Set<String> devices) {
        return MeasMeasuredEmissions.builder()
                .measurementDevicesOrMethods(devices)
                .tier(MeasMeasuredEmissionsTier.TIER_4)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                .build();
    }
}
