package uk.gov.pmrv.api.permit.domain.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import javax.validation.Valid;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "#exist == (#procedureForm != null)", message = "permit.procedureOptionalForm.exist")
public class ProcedureOptionalForm {

    private boolean exist;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private ProcedureForm procedureForm;

}
