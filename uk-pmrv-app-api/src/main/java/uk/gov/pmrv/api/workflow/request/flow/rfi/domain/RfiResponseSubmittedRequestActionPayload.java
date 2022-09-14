package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{#rfiQuestionPayload.questions.size() == #rfiResponsePayload.answers.size()}", message = "rfi.questions.same.size.with.answers")
public class RfiResponseSubmittedRequestActionPayload extends RequestActionPayload {
    
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
        return this.rfiAttachments;
    }
}
