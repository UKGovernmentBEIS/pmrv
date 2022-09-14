package uk.gov.pmrv.api.account.domain.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaExternalContactRegistrationDTO {

    @Size(max = 100)
    @NotBlank
    private String name;

    @Email
    @Size(max = 255)
    @NotBlank
    private String email;

    @Size(max = 100)
    @NotBlank
    private String description;
}
