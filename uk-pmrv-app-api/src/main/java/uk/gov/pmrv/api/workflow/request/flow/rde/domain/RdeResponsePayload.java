package uk.gov.pmrv.api.workflow.request.flow.rde.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RdeResponsePayload {

    @NotNull
    @Future
    private LocalDate currentDueDate;

    @NotNull
    @Future
    private LocalDate proposedDueDate;
}
