package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

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
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitSurrenderApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private PermitSurrender permitSurrender;

    @Builder.Default
    private Map<UUID, String> permitSurrenderAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getPermitSurrenderAttachments();
    }
}
