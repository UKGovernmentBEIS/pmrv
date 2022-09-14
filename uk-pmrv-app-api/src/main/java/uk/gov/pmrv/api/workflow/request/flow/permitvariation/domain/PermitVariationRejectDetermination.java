package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.RejectDetermination;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PermitVariationRejectDetermination extends RejectDetermination implements PermitVariationDeterminateable {

}