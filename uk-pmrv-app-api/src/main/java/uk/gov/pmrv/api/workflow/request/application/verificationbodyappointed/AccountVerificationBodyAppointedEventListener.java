package uk.gov.pmrv.api.workflow.request.application.verificationbodyappointed;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.event.AccountVerificationBodyAppointedEvent;

@RequiredArgsConstructor
@Component
public class AccountVerificationBodyAppointedEventListener {
    
    private final RequestVerificationBodyService requestVerificationBodyService;

    //TODO: CHECK WHAT NEED TO BE DONE AND IN WHICH REQUESTS
    @EventListener
    public void onAccountVerificationBodyAppointedEvent(AccountVerificationBodyAppointedEvent event) {
        requestVerificationBodyService.appointVerificationBodyToRequestsOfAccount(event.getVerificationBodyId(), event.getAccountId());
    }
}
