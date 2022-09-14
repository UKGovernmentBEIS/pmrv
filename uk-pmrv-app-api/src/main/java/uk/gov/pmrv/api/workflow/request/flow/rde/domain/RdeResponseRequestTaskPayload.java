package uk.gov.pmrv.api.workflow.request.flow.rde.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RdeResponseRequestTaskPayload extends RequestTaskPayload {

    @NotNull
    @Valid
    private RdeResponsePayload rdeResponsePayload;
}
