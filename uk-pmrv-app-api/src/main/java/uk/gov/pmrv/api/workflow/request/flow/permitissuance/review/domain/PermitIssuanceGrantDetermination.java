package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.GrantDetermination;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PermitIssuanceGrantDetermination extends GrantDetermination implements PermitIssuanceDeterminateable {
    
}
