package uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
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
public class PermitCessationSubmitRequestTaskPayload extends RequestTaskPayload {

    @JsonUnwrapped
    private PermitCessationContainer cessationContainer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean cessationCompleted;
}
