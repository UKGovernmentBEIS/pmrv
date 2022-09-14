package uk.gov.pmrv.api.migration.permit.emissionsummaries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pmrv.api.migration.permit.regulatedactivities.RegulatedActivitiesMapper;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitIdSection;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummaries;
import uk.gov.pmrv.api.permit.domain.emissionsummaries.EmissionSummary;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmissionSummariesMigrationMapper {

    private static final Logger logger = LoggerFactory.getLogger(EmissionSummariesMigrationMapper.class);

    public static EmissionSummaries toEmissionSummaries(List<EtsEmissionSummary> etsEmissionSummaries, Permit permit) {
        Map<String, String> permitSourceStreamsReferenceToUuidMap = new HashMap<>();
        Map<String, String> permitEmissionSourcesReferenceToUuidMap = new HashMap<>();
        Map<String, String> permitEmissionPointsReferenceToUuidMap = new HashMap<>();
        Map<RegulatedActivityType, String> permitRegulatedActivitiesReferenceToUuidMap = new EnumMap<>(RegulatedActivityType.class);

        initializeMaps(permit, permitSourceStreamsReferenceToUuidMap, permitEmissionSourcesReferenceToUuidMap,
                permitEmissionPointsReferenceToUuidMap, permitRegulatedActivitiesReferenceToUuidMap);

        List<EmissionSummary> emissionSummaries = etsEmissionSummaries.stream()
                .map(etsEmissionSummary -> {
                    EmissionSummary summary = EmissionSummary.builder()
                            .sourceStream(Optional.ofNullable(permitSourceStreamsReferenceToUuidMap
                                    .get(etsEmissionSummary.getSourceStream()))
                                    .orElse(etsEmissionSummary.getSourceStream()))
                            .emissionSources(
                                    etsEmissionSummary.getEmissionSources().stream()
                                            .map(emiissionSource -> Optional.ofNullable(permitEmissionSourcesReferenceToUuidMap
                                                    .get(emiissionSource))
                                                    .orElse(emiissionSource))
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toSet()))
                            .emissionPoints(etsEmissionSummary.getEmissionPoints().stream()
                                    .map(emissionPoint -> Optional.ofNullable(permitEmissionPointsReferenceToUuidMap
                                            .get(emissionPoint))
                                            .orElse(emissionPoint))
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toSet()))
                            .regulatedActivity(Optional.ofNullable(permitRegulatedActivitiesReferenceToUuidMap
                                    .get(RegulatedActivitiesMapper.resolvePmrvRegulatedActivityType(etsEmissionSummary.getRegulatedActivity())))
                                    .orElse(etsEmissionSummary.getRegulatedActivity()))
                            .excludedRegulatedActivity(etsEmissionSummary.isExcludedRegulatedActivity())
                            .build();
                    logInvalidSummary(etsEmissionSummary, summary);
                    return summary;
                })
                .collect(Collectors.toList());
        logBusinessValidationErrors(etsEmissionSummaries, permit, emissionSummaries);

        return EmissionSummaries.builder()
                .emissionSummaries(emissionSummaries)
                .build();
    }

    private static void logBusinessValidationErrors(List<EtsEmissionSummary> etsEmissionSummaries, Permit permit, List<EmissionSummary> emissionSummaries) {
        boolean containsAllSourceStreams = emissionSummaries.stream()
                .map(EmissionSummary::getSourceStream)
                .collect(Collectors.toList()).containsAll(permit.getSourceStreams().getSourceStreams().stream()
                        .map(PermitIdSection::getId).collect(Collectors.toList()));
        boolean containsAllEmissionSources = emissionSummaries.stream()
                .flatMap(summary -> summary.getEmissionSources().stream())
                .collect(Collectors.toList()).containsAll(permit.getEmissionSources().getEmissionSources().stream()
                        .map(PermitIdSection::getId).collect(Collectors.toList()));
        boolean containsAllEmissionPoints = emissionSummaries.stream()
                .flatMap(summary -> summary.getEmissionPoints().stream())
                .collect(Collectors.toList()).containsAll(permit.getEmissionPoints().getEmissionPoints().stream()
                        .map(PermitIdSection::getId).collect(Collectors.toList()));
        boolean containsAllRegulatedActivities = emissionSummaries.stream()
                .map(EmissionSummary::getRegulatedActivity)
                .collect(Collectors.toList()).containsAll(permit.getRegulatedActivities().getRegulatedActivities().stream()
                        .map(PermitIdSection::getId).collect(Collectors.toList()));
        if (!containsAllSourceStreams) {
            logger.warn("emitterId: {}, emitter_display_id: {}, not All SourceStreams are used", etsEmissionSummaries.get(0).getEtsAccountId(), etsEmissionSummaries.get(0).getEmitterDisplayId());
        }
        if (!containsAllEmissionSources) {
            logger.warn("emitterId: {}, emitter_display_id: {}, not All EmissionSources are used", etsEmissionSummaries.get(0).getEtsAccountId(), etsEmissionSummaries.get(0).getEmitterDisplayId());
        }
        if (!containsAllEmissionPoints) {
            logger.warn("emitterId: {}, emitter_display_id: {}, not All EmissionPoints are used", etsEmissionSummaries.get(0).getEtsAccountId(), etsEmissionSummaries.get(0).getEmitterDisplayId());
        }
        if (!containsAllRegulatedActivities) {
            logger.warn("emitterId: {}, emitter_display_id: {}, not All RegulatedActivities are used", etsEmissionSummaries.get(0).getEtsAccountId(), etsEmissionSummaries.get(0).getEmitterDisplayId());
        }
    }

    private static void logInvalidSummary(EtsEmissionSummary etsEmissionSummary, EmissionSummary summary) {
        if (summary.getEmissionPoints().isEmpty() ||
                summary.getSourceStream() == null ||
                (summary.getRegulatedActivity() == null && !summary.isExcludedRegulatedActivity()) ||
                summary.getEmissionSources().isEmpty()) {
            logger.warn("emitterId: {}, emitter_display_id: {}, sourceStream: {}, emissionSource: {}, emissionPoints: {}, regulatedActivity: {}, isExcluded: {}",
                    etsEmissionSummary.getEtsAccountId(),
                    etsEmissionSummary.getEmitterDisplayId(),
                    etsEmissionSummary.getSourceStream(),
                    etsEmissionSummary.getEmissionSources(),
                    etsEmissionSummary.getEmissionPoints(),
                    etsEmissionSummary.getRegulatedActivity(),
                    etsEmissionSummary.isExcludedRegulatedActivity());
        }
    }

    private static void initializeMaps(Permit permit, Map<String, String> permitSourceStreams, Map<String, String> permitEmissionSources,
                                       Map<String, String> permitEmissionPoints, Map<RegulatedActivityType, String> permitRegulatedActivities) {
        Optional.ofNullable(permit.getSourceStreams())
                .ifPresent(sourceStreams ->
                        sourceStreams.getSourceStreams()
                                .forEach(sourceStream -> permitSourceStreams.put(sourceStream.getReference(), sourceStream.getId())));

        Optional.ofNullable(permit.getEmissionSources())
                .ifPresent(emissionSources ->
                        emissionSources.getEmissionSources()
                                .forEach(emissionSource -> permitEmissionSources.put(emissionSource.getReference(), emissionSource.getId())));

        Optional.ofNullable(permit.getEmissionPoints())
                .ifPresent(emissionPoints ->
                        emissionPoints.getEmissionPoints()
                                .forEach(emissionPoint -> permitEmissionPoints.put(emissionPoint.getReference(), emissionPoint.getId())));

        Optional.ofNullable(permit.getRegulatedActivities())
                .ifPresent(regulatedActivities ->
                        regulatedActivities.getRegulatedActivities()
                                .forEach(regulatedActivity -> permitRegulatedActivities.put(regulatedActivity.getType(), regulatedActivity.getId())));
    }
}
