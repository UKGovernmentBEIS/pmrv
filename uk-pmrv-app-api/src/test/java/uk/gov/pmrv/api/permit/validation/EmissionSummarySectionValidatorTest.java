package uk.gov.pmrv.api.permit.validation;

import java.util.Collections;

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
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

@ExtendWith(MockitoExtension.class)
class EmissionSummarySectionValidatorTest {

    @InjectMocks
    private EmissionSummarySectionValidator validator;

    @Mock
    private PermitReferenceService permitReferenceService;

    private static String sourceStreamId1, sourceStreamId2,
            emissionSourceId1, emissionSourceId2,
            emissionPointId1, emissionPointId2,
            regulatedActivityId1, regulatedActivityId2;
    private static SourceStream sourceStream1, sourceStream2;
    private static EmissionSource emissionSource1, emissionSource2;
    private static EmissionPoint emissionPoint1, emissionPoint2;
    private static RegulatedActivity regulatedActivity1, regulatedActivity2;
    @BeforeAll
    static void setup() {
        sourceStreamId1 = UUID.randomUUID().toString();
        sourceStreamId2 = UUID.randomUUID().toString();
        emissionSourceId1 = UUID.randomUUID().toString();
        emissionSourceId2 = UUID.randomUUID().toString();
        emissionPointId1 = UUID.randomUUID().toString();
        emissionPointId2 = UUID.randomUUID().toString();
        regulatedActivityId1 = UUID.randomUUID().toString();
        regulatedActivityId2 = UUID.randomUUID().toString();

        sourceStream1 = SourceStream.builder().id(sourceStreamId1).reference("sourceStream1").build();
        sourceStream2 = SourceStream.builder().id(sourceStreamId2).reference("sourceStream2").build();
        emissionSource1 = EmissionSource.builder().id(emissionSourceId1).reference("emissionSource1").build();
        emissionSource2 = EmissionSource.builder().id(emissionSourceId2).reference("emissionSource2").build();
        emissionPoint1 = EmissionPoint.builder().id(emissionPointId1).reference("emissionPoint1").build();
        emissionPoint2 = EmissionPoint.builder().id(emissionPointId2).reference("emissionPoint2").build();
        regulatedActivity1 = RegulatedActivity.builder().id(regulatedActivityId1).type(RegulatedActivityType.COMBUSTION).build();
        regulatedActivity2 = RegulatedActivity.builder().id(regulatedActivityId2).type(RegulatedActivityType.ADIPIC_ACID_PRODUCTION).build();
    }

    @Test
    void validateSection() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId1)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .regulatedActivity(regulatedActivityId1)
            .excludedRegulatedActivity(false)
            .build();

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_source_stream() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId2)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .regulatedActivity(regulatedActivityId1)
            .excludedRegulatedActivity(false)
            .build();

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                        List.of(sourceStream2.getId()))));

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                    List.of(sourceStream2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_emission_source() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId1)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .regulatedActivity(regulatedActivityId1)
            .excludedRegulatedActivity(false)
            .build();

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                        List.of(emissionSource2.getId()))));

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                    List.of(emissionSource2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_emission_point() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId1)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .regulatedActivity(regulatedActivityId1)
            .excludedRegulatedActivity(false)
            .build();

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                        List.of(emissionPoint2.getId()))));

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                    List.of(emissionPoint2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_regulated_activity() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId1)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .regulatedActivity(regulatedActivityId2)
            .excludedRegulatedActivity(false)
            .build();

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST))
                .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_REGULATED_ACTIVITY,
                        List.of(regulatedActivity2.getId()))));

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_REGULATED_ACTIVITY,
                    List.of(regulatedActivity2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_with_excluded_regulated_activity_flag() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId1)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .excludedRegulatedActivity(true)
            .build();

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_with_excluded_regulated_activity_flag_and_regulated_activity() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId1)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .excludedRegulatedActivity(true)
            .build();
        emissionSummary.setRegulatedActivity(regulatedActivityId1);

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(EmissionSummary.class.getSimpleName(), 
                                PermitViolation.PermitViolationMessage.EMISSION_SUMMARY_INVALID_EXCLUDED_ACTIVITY,
                                Collections.emptyList().toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_invalid_emission_summary() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1)).build())
            .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
            .build();

        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        EmissionSummary emissionSummary = EmissionSummary.builder()
            .sourceStream(sourceStreamId2)
            .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
            .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
            .regulatedActivity(regulatedActivityId1)
            .excludedRegulatedActivity(false)
            .build();

        when(permitReferenceService.validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                        List.of(sourceStream2.getId()))));

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                        List.of(emissionSource2.getId()))));

        when(permitReferenceService.validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                        List.of(emissionPoint2.getId()))));

        when(permitReferenceService.validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS))
                .thenReturn(Optional.empty());

        PermitValidationResult result = validator.validate(emissionSummary, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(3);
        assertThat(permitViolations).containsExactlyInAnyOrder(
            new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                    List.of(sourceStream2.getId()).toArray()),
            new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                    List.of(emissionSource2.getId()).toArray()),
            new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                    List.of(emissionPoint2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getSourceStreamsIds(),
                List.of(emissionSummary.getSourceStream()),
                PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionSourcesIds(),
                emissionSummary.getEmissionSources(),
                PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getEmissionPointsIds(),
                emissionSummary.getEmissionPoints(),
                PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permit.getRegulatedActivitiesIds(),
                List.of(emissionSummary.getRegulatedActivity()),
                PermitReferenceService.Rule.REGULATED_ACTIVITY_EXISTS);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_noSection() {
        PermitContainer permitContainer = PermitContainer.builder()
                .permit(Permit.builder().build())
                .build();

        PermitValidationResult result = validator.validate(null, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }
}