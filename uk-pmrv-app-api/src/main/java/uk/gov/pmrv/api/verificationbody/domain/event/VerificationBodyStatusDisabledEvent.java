package uk.gov.pmrv.api.verificationbody.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class VerificationBodyStatusDisabledEvent {

    private final Set<Long> verificationBodyIds;
}
