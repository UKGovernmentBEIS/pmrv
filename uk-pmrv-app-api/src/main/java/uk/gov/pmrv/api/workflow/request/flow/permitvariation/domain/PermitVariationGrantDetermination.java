package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.GrantDetermination;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationGrantDetermination extends GrantDetermination implements PermitVariationDeterminateable {
    
	@Size(max = 10000)
	@NotBlank
    private String logChanges;
}
