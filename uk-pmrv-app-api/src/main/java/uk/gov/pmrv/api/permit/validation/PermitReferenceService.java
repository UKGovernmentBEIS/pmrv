package uk.gov.pmrv.api.permit.validation;

import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.ATTACHMENT_NOT_FOUND;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_POINT_SHOULD_EXIST;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_EMISSION_SOURCE_SHOULD_EXIST;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_REGULATED_ACTIVITY_SHOULD_EXIST;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.EMISSION_SUMMARIES_SOURCE_STREAM_SHOULD_EXIST;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_REGULATED_ACTIVITY;
import static uk.gov.pmrv.api.permit.domain.PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.tuple.Pair;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.files.attachments.service.FileAttachmentService;
import uk.gov.pmrv.api.permit.domain.PermitViolation;

@Service
@RequiredArgsConstructor
public class PermitReferenceService {

    private final FileAttachmentService fileAttachmentService;

    public enum Rule {

        SOURCE_STREAM_EXISTS,
        EMISSION_SOURCES_EXIST,
        EMISSION_POINTS_EXIST,
        REGULATED_ACTIVITY_EXISTS,
        MEASUREMENT_DEVICES_OR_METHODS_EXIST,
        SOURCE_STREAMS_USED,
        EMISSION_SOURCES_USED,
        EMISSION_POINTS_USED,
        REGULATED_ACTIVITIES_USED,
        ;

        private static final Map<Rule, PermitViolation.PermitViolationMessage> MAP =
            ImmutableMap.<Rule, PermitViolation.PermitViolationMessage>builder()
                .put(SOURCE_STREAM_EXISTS, INVALID_SOURCE_STREAM)
                .put(EMISSION_SOURCES_EXIST, INVALID_EMISSION_SOURCE)
                .put(EMISSION_POINTS_EXIST, INVALID_EMISSION_POINT)
                .put(REGULATED_ACTIVITY_EXISTS, INVALID_REGULATED_ACTIVITY)
                .put(MEASUREMENT_DEVICES_OR_METHODS_EXIST, INVALID_MEASUREMENT_DEVICE_OR_METHOD)
                .put(SOURCE_STREAMS_USED, EMISSION_SUMMARIES_SOURCE_STREAM_SHOULD_EXIST)
                .put(EMISSION_SOURCES_USED, EMISSION_SUMMARIES_EMISSION_SOURCE_SHOULD_EXIST)
                .put(EMISSION_POINTS_USED, EMISSION_SUMMARIES_EMISSION_POINT_SHOULD_EXIST)
                .put(REGULATED_ACTIVITIES_USED, EMISSION_SUMMARIES_REGULATED_ACTIVITY_SHOULD_EXIST)
            .build();
        
        public static PermitViolation.PermitViolationMessage getViolationMessage(final Rule type) {
            return MAP.get(type);
        }
    }

    public Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> validateExistenceInPermit(
        final Collection<String> referencesInPermit,
        final Collection<String> referencesInSection,
        final Rule rule) {

        final List<String> diff = new ArrayList<>(referencesInSection);
        diff.removeAll(referencesInPermit);
        diff.remove(null);
        if (!diff.isEmpty()) {
            final PermitViolation.PermitViolationMessage message = Rule.getViolationMessage(rule);
            return Optional.of(Pair.of(message, diff));
        }
        return Optional.empty();
    }

    public Optional<Pair<PermitViolation.PermitViolationMessage, Map<String, String>>> validateAllInPermitAreUsed(
        final Map<String, String> referencesInPermit,
        final Collection<String> referencesInSections,
        final Rule rule) {
        
        final Map<String, String> diff = new HashMap<>(referencesInPermit).entrySet().stream()
            .filter(entry -> !referencesInSections.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (!diff.isEmpty()) {
            final PermitViolation.PermitViolationMessage message = Rule.getViolationMessage(rule);
            return Optional.of(Pair.of(message, diff));
        }
        return Optional.empty();
    }

    public Optional<Pair<PermitViolation.PermitViolationMessage, List<String>>> validateFilesExist(
            final Set<UUID> filesInPermitSections, final Set<UUID> files) {
        if (filesInPermitSections.isEmpty()) {
            return Optional.empty();
        }
        
        if (!files.containsAll(filesInPermitSections) || 
                !fileAttachmentService.fileAttachmentsExist(filesInPermitSections.stream().map(UUID::toString).collect(Collectors.toSet()))) {
            return Optional.of(Pair.of(ATTACHMENT_NOT_FOUND, Collections.emptyList()));
        } else {
            return Optional.empty();
        }
    }
}
