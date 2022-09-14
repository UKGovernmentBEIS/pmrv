package uk.gov.pmrv.api.account.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalEntityInfoDTO {

    /** The id. */
    @NotNull
    private Long id;

    /** The Legal Entity name. */
    @NotBlank(message="{legalEntity.name.notEmpty}")
    @Size(max = 255, message = "{legalEntity.name.typeMismatch}")
    private String name;
}
