package uk.gov.pmrv.api.permit.domain.confidentialitystatement;

import java.util.List;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#confidentialSections?.size() gt 0)}", message = "permit.confidentialityStatement.exist")
public class ConfidentialityStatement implements PermitSection {

    private boolean exist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ConfidentialSection> confidentialSections;

}
