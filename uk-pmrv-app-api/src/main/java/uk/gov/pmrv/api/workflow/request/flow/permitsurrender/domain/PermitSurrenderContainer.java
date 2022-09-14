package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitSurrenderContainer {

    @Valid
    @NotNull
    private PermitSurrender permitSurrender;

    @Builder.Default
    private Map<UUID, String> permitSurrenderAttachments = new HashMap<>();
}
