package uk.gov.pmrv.api.permit.domain.envmanagementsystem;

import javax.validation.constraints.Size;

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
@SpELExpression(expression = "{#exist == (#certified != null)}", message = "permit.environmentalManagementSystem.exist")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#certified) == (#certificationStandard != null)}", message = "permit.environmentalManagementSystem.certified")
public class EnvironmentalManagementSystem implements PermitSection {

    private boolean exist;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean certified;

    @Size(max=1000)
    private String certificationStandard;

}
