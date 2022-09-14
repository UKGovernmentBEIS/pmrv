package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{#rfiQuestionPayload.questions.size() == #rfiResponsePayload.answers.size()}", message = "rfi.questions.same.size.with.answers")
public class RfiResponseSubmitRequestTaskPayload extends RequestTaskPayload implements RequestTaskPayloadRfiAttachable {

    @NotNull
    @Valid
    private RfiQuestionPayload rfiQuestionPayload;

    @NotNull
    @Valid
    private RfiResponsePayload rfiResponsePayload;

    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getRfiAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        
        final Set<UUID> responseFiles = rfiResponsePayload != null ? rfiResponsePayload.getFiles() : new HashSet<>();
        
        return Stream.of(rfiQuestionPayload.getFiles(), responseFiles)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }
}