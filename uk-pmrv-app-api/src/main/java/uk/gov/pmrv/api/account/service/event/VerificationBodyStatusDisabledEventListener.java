package uk.gov.pmrv.api.account.service.event;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.account.service.AccountVerificationBodyUnappointService;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusDisabledEvent;

@RequiredArgsConstructor
@Component
public class VerificationBodyStatusDisabledEventListener {

    private final AccountVerificationBodyUnappointService accountVerificationBodyUnappointService;

    @Order(2)
    @EventListener(VerificationBodyStatusDisabledEvent.class)
    public void onVerificationBodyStatusDisabledEvent(VerificationBodyStatusDisabledEvent event) {
        accountVerificationBodyUnappointService.unappointAccountsAppointedToVerificationBody(event.getVerificationBodyIds());
    }
}
