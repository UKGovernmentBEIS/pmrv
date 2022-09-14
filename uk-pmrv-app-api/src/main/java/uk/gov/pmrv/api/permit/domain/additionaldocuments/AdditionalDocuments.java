package uk.gov.pmrv.api.permit.domain.additionaldocuments;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#documents?.size() gt 0)}", message = "permit.additionalDocuments.exist")
public class AdditionalDocuments implements PermitSection {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> documents;

}
