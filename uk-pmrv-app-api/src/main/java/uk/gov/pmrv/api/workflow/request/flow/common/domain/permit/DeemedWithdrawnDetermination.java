package uk.gov.pmrv.api.workflow.request.flow.common.domain.permit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public abstract class DeemedWithdrawnDetermination extends Determination {
}