package uk.gov.pmrv.api.verificationbody.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class VerificationBodyDeletedEvent {

    private final Long verificationBodyId;
}
