package uk.gov.pmrv.api.workflow.request.flow.common.domain.permit;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Determination implements Determinateable {

    private DeterminationType type;

    @Size(max = 10000)
    private String reason;
}
