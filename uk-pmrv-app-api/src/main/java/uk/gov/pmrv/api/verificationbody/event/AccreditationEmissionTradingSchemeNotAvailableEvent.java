package uk.gov.pmrv.api.verificationbody.event;

import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AccreditationEmissionTradingSchemeNotAvailableEvent {

    private final Long verificationBodyId;
    private final Set<EmissionTradingScheme> notAvailableAccreditationEmissionTradingSchemes;
}
