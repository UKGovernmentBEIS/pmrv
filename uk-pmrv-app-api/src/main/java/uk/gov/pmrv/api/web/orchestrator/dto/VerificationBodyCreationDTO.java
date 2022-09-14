package uk.gov.pmrv.api.web.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationBodyCreationDTO {

    @JsonUnwrapped
    @Valid
    private VerificationBodyEditDTO verificationBody;

    @NotNull(message = "{verificationBody.adminUser.notEmpty}")
    @Valid
    private AdminVerifierUserInvitationDTO adminVerifierUserInvitation;
}
