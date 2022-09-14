package uk.gov.pmrv.api.workflow.request.flow.rde.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RdeSubmitRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    @Valid
    private RdePayload rdePayload;
}
