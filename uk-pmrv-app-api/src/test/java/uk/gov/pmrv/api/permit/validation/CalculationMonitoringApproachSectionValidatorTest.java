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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

@ExtendWith(MockitoExtension.class)
class CalculationMonitoringApproachSectionValidatorTest {

    @InjectMocks
    private CalculationMonitoringApproachSectionValidator validator;

    @Mock
    private PermitReferenceService permitReferenceService;

    private String sourceStreamId1 = UUID.randomUUID().toString();
    private String sourceStreamId2 = UUID.randomUUID().toString();
    private String emissionSourceId1 = UUID.randomUUID().toString();
    private String emissionSourceId2 = UUID.randomUUID().toString();
    private String measurementDeviceOrMethodId1 = UUID.randomUUID().toString();
    private String measurementDeviceOrMethodId2 = UUID.randomUUID().toString();
    
    private SourceStream sourceStream1 = SourceStream.builder().id(sourceStreamId1).reference("sourceStream1").build();
    private SourceStream sourceStream2 = SourceStream.builder().id(sourceStreamId2).reference("sourceStream2").build();
    private EmissionSource emissionSource1 = EmissionSource.builder().id(emissionSourceId1).reference("emissionSource1").build();
    private EmissionSource emissionSource2 = EmissionSource.builder().id(emissionSourceId2).reference("emissionSource2").build();
    private MeasurementDeviceOrMethod measurementDeviceOrMethod1 = MeasurementDeviceOrMethod.builder().id(measurementDeviceOrMethodId1).build();
    private MeasurementDeviceOrMethod measurementDeviceOrMethod2 = MeasurementDeviceOrMethod.builder().id(measurementDeviceOrMethodId2).build();

    @Test
    void validatePermitContainer_whenEverythingExists_thenAllow() {
        final CalculationMonitoringApproach monitoringApproach = buildCalculationMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(measurementDeviceOrMethodId1, measurementDeviceOrMethodId2));
        
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(
                        EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .measurementDevicesOrMethods(
                    MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION, monitoringApproach))
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
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getActivityData().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validatePermitContainer_whenNoApproachExists_thenAllow() {
        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
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
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .measurementDevicesOrMethods(
                    MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                    .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final CalculationMonitoringApproach monitoringApproach = buildCalculationMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(measurementDeviceOrMethodId1, measurementDeviceOrMethodId2));

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
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getActivityData().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_whenInvalidSourceStream_thenPermitViolation() {
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .measurementDevicesOrMethods(
                    MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final CalculationMonitoringApproach monitoringApproach = buildCalculationMonitoringApproach(sourceStreamId2,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(measurementDeviceOrMethodId1, measurementDeviceOrMethodId2));

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
            new PermitViolation(CalculationMonitoringApproach.class.getSimpleName(),
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
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getActivityData().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_whenInvalidEmissionSource_thenPermitViolation() {
        final Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1)).build())
                .measurementDevicesOrMethods(
                    MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
                .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final CalculationMonitoringApproach monitoringApproach = buildCalculationMonitoringApproach(sourceStreamId1,
                Set.of(emissionSourceId1, emissionSourceId2),
                Set.of(measurementDeviceOrMethodId1, measurementDeviceOrMethodId2));

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
            new PermitViolation(CalculationMonitoringApproach.class.getSimpleName(),
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
                permit.getMeasurementDevicesOrMethodsIds(),
                monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getActivityData().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_whenInvalidMeasurementDeviceOrMethod_thenPermitViolation() {
        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .measurementDevicesOrMethods(
                MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                    List.of(measurementDeviceOrMethod1)).build())
            .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final CalculationMonitoringApproach monitoringApproach = buildCalculationMonitoringApproach(sourceStreamId2,
            Set.of(emissionSourceId1, emissionSourceId2),
            Set.of(measurementDeviceOrMethodId1, measurementDeviceOrMethodId2));

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
            permit.getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getActivityData().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST))
            .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                List.of(measurementDeviceOrMethod2.getId()))));

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(CalculationMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                List.of(measurementDeviceOrMethod2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            List.of(monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getSourceStream()),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getSourceStreamCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getSourceStreamCategoryAppliedTiers().get(0).getActivityData().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_whenSectionIsNull_thenAllow() {
        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .measurementDevicesOrMethods(
                MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                    List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();
        final PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        final PermitValidationResult result = validator.validate(null, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    private CalculationMonitoringApproach buildCalculationMonitoringApproach(final String sourceStream,
                                                                             final Set<String> emissionSources,
                                                                             final Set<String> measurementDeviceOrMethods) {
        return CalculationMonitoringApproach.builder()
            .sourceStreamCategoryAppliedTiers(List.of(CalculationSourceStreamCategoryAppliedTier.builder()
                    .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                        .sourceStream(sourceStream)
                        .emissionSources(emissionSources)
                        .build())
                    .activityData(CalculationActivityData.builder()
                        .measurementDevicesOrMethods(measurementDeviceOrMethods).build())
                .build()))
            .build();
    }
}
