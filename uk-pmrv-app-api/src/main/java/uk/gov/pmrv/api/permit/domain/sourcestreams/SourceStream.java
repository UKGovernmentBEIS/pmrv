package uk.gov.pmrv.api.permit.domain.sourcestreams;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitIdSection;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#description eq 'OTHER') == (#otherDescriptionName != null)}", 
    message = "permit.sourceStream.otherDescriptionName")
public class SourceStream extends PermitIdSection {
    
    @NotBlank
    @Size(max = 10000)
    private String reference;

    @NotNull
    private SourceStreamDescription description;

    @Size(max = 300)
    private String otherDescriptionName;
    
    @NotNull
    private SourceStreamType type;

}
