package uk.gov.pmrv.api.permit.validation;

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
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionSummariesSectionValidatorTest {

    @InjectMocks
    private EmissionSummariesSectionValidator validator;

    @Mock
    private EmissionSummarySectionValidator emissionSummarySectionValidator;
    
    @Mock
    private PermitReferenceService permitReferenceService;
    

    private static String sourceStreamId1, sourceStreamId2,
            emissionSourceId1, emissionSourceId2,
            emissionPointId1, emissionPointId2,
            regulatedActivityId1, regulatedActivityId2, regulatedActivityId3;
    private static SourceStream sourceStream1, sourceStream2, sourceStream3;
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
        regulatedActivityId3 = UUID.randomUUID().toString();

        sourceStream1 = SourceStream.builder().id(sourceStreamId1).reference("sourceStream1").build();
        sourceStream2 = SourceStream.builder().id(sourceStreamId2).reference("sourceStream2").build();
        sourceStream3 = SourceStream.builder().id(UUID.randomUUID().toString()).reference("sourceStream2").build();
        emissionSource1 = EmissionSource.builder().id(emissionSourceId1).reference("emissionSource1").build();
        emissionSource2 = EmissionSource.builder().id(emissionSourceId2).reference("emissionSource2").build();
        emissionPoint1 = EmissionPoint.builder().id(emissionPointId1).reference("emissionPoint1").build();
        emissionPoint2 = EmissionPoint.builder().id(emissionPointId2).reference("emissionPoint2").build();
        regulatedActivity1 = RegulatedActivity.builder().id(regulatedActivityId1).type(RegulatedActivityType.COMBUSTION).build();
        regulatedActivity2 = RegulatedActivity.builder().id(regulatedActivityId2).type(RegulatedActivityType.ADIPIC_ACID_PRODUCTION).build();
    }

    @Test
    void validate() {
        EmissionSummary emissionSummary1 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1))
                .emissionPoints(Set.of(emissionPointId1))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummary emissionSummary2 = EmissionSummary.builder()
                .sourceStream(sourceStreamId2)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId2)
                .build();

        EmissionSummaries emissionSummaries = EmissionSummaries.builder()
                .emissionSummaries(List.of(emissionSummary1, emissionSummary2))
                .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .emissionSummaries(emissionSummaries)
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        when(emissionSummarySectionValidator.validate(emissionSummary1, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary2, permitContainer)).thenReturn(PermitValidationResult.validPermit());

        PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());

        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary1, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary2, permitContainer);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_source_stream_not_used() {
        EmissionSummary emissionSummary1 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummary emissionSummary2 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId2)
                .build();

        EmissionSummaries emissionSummaries = EmissionSummaries.builder()
                .emissionSummaries(List.of(emissionSummary1, emissionSummary2))
                .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .emissionSummaries(emissionSummaries)
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        when(emissionSummarySectionValidator.validate(emissionSummary1, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary2, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_SOURCE_STREAM_SHOULD_EXIST,
                        Map.of(sourceStream2.getId(), sourceStream2.getReference()))));

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(EmissionSummaries.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_SOURCE_STREAM_SHOULD_EXIST,
                        Map.of(sourceStream2.getId(), sourceStream2.getReference()).entrySet().toArray()));

        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary1, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary2, permitContainer);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_emission_source_not_used() {
        EmissionSummary emissionSummary1 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummary emissionSummary2 = EmissionSummary.builder()
                .sourceStream(sourceStreamId2)
                .emissionSources(Set.of(emissionSourceId1))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId2)
                .build();

        EmissionSummaries emissionSummaries = EmissionSummaries.builder()
                .emissionSummaries(List.of(emissionSummary1, emissionSummary2))
                .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .emissionSummaries(emissionSummaries)
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        when(emissionSummarySectionValidator.validate(emissionSummary1, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary2, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_SOURCE_SHOULD_EXIST,
                        Map.of(emissionSource2.getId(), emissionSource2.getReference()))));

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(EmissionSummaries.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_SOURCE_SHOULD_EXIST,
                        Map.of(emissionSource2.getId(), emissionSource2.getReference()).entrySet().toArray()));

        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary1, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary2, permitContainer);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_emission_point_not_used() {
        EmissionSummary emissionSummary1 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummary emissionSummary2 = EmissionSummary.builder()
                .sourceStream(sourceStreamId2)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1))
                .regulatedActivity(regulatedActivityId2)
                .build();

        EmissionSummaries emissionSummaries = EmissionSummaries.builder()
                .emissionSummaries(List.of(emissionSummary1, emissionSummary2))
                .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .emissionSummaries(emissionSummaries)
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        when(emissionSummarySectionValidator.validate(emissionSummary1, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary2, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_POINT_SHOULD_EXIST,
                        Map.of(emissionPoint2.getId(), emissionPoint2.getReference()))));

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(EmissionSummaries.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_POINT_SHOULD_EXIST,
                        Map.of(emissionPoint2.getId(), emissionPoint2.getReference()).entrySet().toArray()));

        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary1, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary2, permitContainer);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_regulated_activity_not_used() {
        EmissionSummary emissionSummary1 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummary emissionSummary2 = EmissionSummary.builder()
                .sourceStream(sourceStreamId2)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummaries emissionSummaries = EmissionSummaries.builder()
                .emissionSummaries(List.of(emissionSummary1, emissionSummary2))
                .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .emissionSummaries(emissionSummaries)
                .build();
        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        when(emissionSummarySectionValidator.validate(emissionSummary1, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary2, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_REGULATED_ACTIVITY_SHOULD_EXIST,
                        Map.of(regulatedActivity2.getId(), regulatedActivity2.getType().toString()))));

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactlyInAnyOrder(
                new PermitViolation(EmissionSummaries.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_REGULATED_ACTIVITY_SHOULD_EXIST,
                        Map.of(regulatedActivity2.getId(), regulatedActivity2.getType().toString()).entrySet().toArray()));

        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary1, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary2, permitContainer);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_invalid_emission_summary() {
        EmissionSummary emissionSummary1 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1))
                .emissionPoints(Set.of(emissionPointId1))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummary emissionSummary2 = EmissionSummary.builder()
                .sourceStream(sourceStreamId2)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId2)
                .build();

        EmissionSummary emissionSummary3 = EmissionSummary.builder()
                .sourceStream(sourceStreamId2)
                .emissionSources(Set.of(emissionSourceId1, emissionSourceId2))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId3)
                .build();

        EmissionSummaries emissionSummaries = EmissionSummaries.builder()
                .emissionSummaries(List.of(emissionSummary1, emissionSummary2, emissionSummary3))
                .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2, sourceStream3)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .emissionSummaries(emissionSummaries)
                .build();

        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        List<PermitViolation> emissionSummary3Violations = List.of(
                new PermitViolation(EmissionSummary.class.getSimpleName(), PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE));

        when(emissionSummarySectionValidator.validate(emissionSummary1, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary2, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary3, permitContainer)).thenReturn(PermitValidationResult.invalidPermit(emissionSummary3Violations));
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_SOURCE_STREAM_SHOULD_EXIST,
                        Map.of(sourceStream3.getId(), sourceStream3.getReference()))));

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(2);
        assertThat(permitViolations).containsExactlyInAnyOrder(
                new PermitViolation(EmissionSummaries.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_SOURCE_STREAM_SHOULD_EXIST,
                        Map.of(sourceStream3.getId(), sourceStream3.getReference()).entrySet().toArray()),
                new PermitViolation(EmissionSummary.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE)
        );

        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary1, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary2, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary3, permitContainer);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_invalid_emission_summaries() {
        EmissionSummary emissionSummary1 = EmissionSummary.builder()
                .sourceStream(sourceStreamId1)
                .emissionSources(Set.of(emissionSourceId1))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummary emissionSummary2 = EmissionSummary.builder()
                .sourceStream(sourceStreamId2)
                .emissionSources(Set.of(emissionSourceId1))
                .emissionPoints(Set.of(emissionPointId1, emissionPointId2))
                .regulatedActivity(regulatedActivityId1)
                .build();

        EmissionSummaries emissionSummaries = EmissionSummaries.builder()
                .emissionSummaries(List.of(emissionSummary1, emissionSummary2))
                .build();

        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .emissionSummaries(emissionSummaries)
                .build();

        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        when(emissionSummarySectionValidator.validate(emissionSummary1, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(emissionSummarySectionValidator.validate(emissionSummary2, permitContainer)).thenReturn(PermitValidationResult.validPermit());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_SOURCE_SHOULD_EXIST,
                        Map.of(emissionSource2.getId(), emissionSource2.getReference()))));
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED))
                .thenReturn(Optional.empty());
        when(permitReferenceService.validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_REGULATED_ACTIVITY_SHOULD_EXIST,
                        Map.of(regulatedActivity2.getId(), regulatedActivity2.getType().toString()))));

        PermitValidationResult result = validator.validate(permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(2);
        assertThat(permitViolations).containsExactlyInAnyOrder(
                new PermitViolation(EmissionSummaries.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_REGULATED_ACTIVITY_SHOULD_EXIST,
                        Map.of(regulatedActivity2.getId(), regulatedActivity2.getType().toString()).entrySet().toArray()),
                new PermitViolation(EmissionSummaries.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_SOURCE_SHOULD_EXIST,
                        Map.of(emissionSource2.getId(), emissionSource2.getReference()).entrySet().toArray()));

        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary1, permitContainer);
        verify(emissionSummarySectionValidator, times(1)).validate(emissionSummary2, permitContainer);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getSourceStreamsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getSourceStream).collect(Collectors.toSet()),
                PermitReferenceService.Rule.SOURCE_STREAMS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionSourcesIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionSources().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_SOURCES_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getEmissionPointsIdRefMap(),
                emissionSummaries.getEmissionSummaries().stream().flatMap(emissionSummary -> emissionSummary.getEmissionPoints().stream()).collect(Collectors.toSet()),
                PermitReferenceService.Rule.EMISSION_POINTS_USED);
        verify(permitReferenceService, times(1)).validateAllInPermitAreUsed(
                permit.getRegulatedActivitiesIdTypeMap(),
                emissionSummaries.getEmissionSummaries().stream().map(EmissionSummary::getRegulatedActivity).collect(Collectors.toSet()),
                PermitReferenceService.Rule.REGULATED_ACTIVITIES_USED);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validate_no_emission_summaries() {
        Permit permit = Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
                .regulatedActivities(RegulatedActivities.builder().regulatedActivities(List.of(regulatedActivity1, regulatedActivity2)).build())
                .build();

        PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());

        verify(emissionSummarySectionValidator, never()).validate(any(), any());
        verify(permitReferenceService, never()).validateAllInPermitAreUsed(anyMap(), anyCollection(), any());
    }
}