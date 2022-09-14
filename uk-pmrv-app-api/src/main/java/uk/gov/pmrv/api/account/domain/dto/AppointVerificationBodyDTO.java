package uk.gov.pmrv.api.account.domain.dto;


import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointVerificationBodyDTO {

    @NotNull
    private Long verificationBodyId;
}
