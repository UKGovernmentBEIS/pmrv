package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PermitSurrenderReviewDeterminationDeemWithdraw extends PermitSurrenderReviewDetermination {
    
}
