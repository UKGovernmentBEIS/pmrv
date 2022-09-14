package uk.gov.pmrv.api.permit.domain.abbreviations;

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
@SpELExpression(expression = "{#exist == (#abbreviationDefinitions?.size() gt 0)}", message = "permit.abbreviations.exist")
public class Abbreviations implements PermitSection {

    private boolean exist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<AbbreviationDefinition> abbreviationDefinitions;

}
