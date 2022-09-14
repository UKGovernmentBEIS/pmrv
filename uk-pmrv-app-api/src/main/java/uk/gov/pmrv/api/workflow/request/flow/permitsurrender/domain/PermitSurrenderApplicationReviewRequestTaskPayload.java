package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitSurrenderApplicationReviewRequestTaskPayload extends RequestTaskPayload implements RequestTaskPayloadRfiAttachable {

    @NotNull
    private PermitSurrender permitSurrender;

    @Builder.Default
    private Map<UUID, String> permitSurrenderAttachments = new HashMap<>();
    
    @NotNull
    private PermitSurrenderReviewDecision reviewDecision;

    @NotNull
    private PermitSurrenderReviewDetermination reviewDetermination;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean reviewDeterminationCompleted;

    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(getPermitSurrenderAttachments(), getRfiAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getPermitSurrender() != null ?
            getPermitSurrender().getAttachmentIds() :
            Collections.emptySet();
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        super.removeAttachments(uuids);
        getRfiAttachments().keySet().removeIf(uuids::contains);
    }
}
