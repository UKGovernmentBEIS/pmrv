package uk.gov.pmrv.api.user.verifier.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class VerifierAdminCreationEvent {

    private Long verificationBodyId;
}
