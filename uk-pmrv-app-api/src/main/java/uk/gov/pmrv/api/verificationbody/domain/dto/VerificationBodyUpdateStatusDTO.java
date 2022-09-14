package uk.gov.pmrv.api.verificationbody.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.verificationbody.domain.dto.validation.StatusPending;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationBodyUpdateStatusDTO {

    @NotNull
    private Long id;

    @NotNull
    @StatusPending
    private VerificationBodyStatus status;
}
