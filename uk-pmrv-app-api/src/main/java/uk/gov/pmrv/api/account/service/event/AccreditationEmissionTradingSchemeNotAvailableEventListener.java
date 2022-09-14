package uk.gov.pmrv.api.account.service.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyUnappointService;
import uk.gov.pmrv.api.verificationbody.event.AccreditationEmissionTradingSchemeNotAvailableEvent;

@RequiredArgsConstructor
@Component
public class AccreditationEmissionTradingSchemeNotAvailableEventListener {
    
    private final AccountVerificationBodyUnappointService accountVerificationBodyUnappointService;

    @EventListener(AccreditationEmissionTradingSchemeNotAvailableEvent.class)
    public void onAccreditationEmissionTradingSchemeNotAvailableEvent(AccreditationEmissionTradingSchemeNotAvailableEvent event) {
        accountVerificationBodyUnappointService
            .unappointAccountsAppointedToVerificationBodyForEmissionTradingSchemes(
                event.getVerificationBodyId(), event.getNotAvailableAccreditationEmissionTradingSchemes());
    }
}
