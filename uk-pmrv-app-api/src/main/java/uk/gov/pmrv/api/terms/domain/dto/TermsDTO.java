package uk.gov.pmrv.api.terms.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The terms DTO.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TermsDTO {

    @NotBlank
    private String url;

    @NotNull
    private Short version;

}
