package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitSurrenderSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private PermitSurrender permitSurrender;

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
