package uk.gov.pmrv.api.workflow.request.core.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.AccountStatusChangedEvent;
import uk.gov.pmrv.api.workflow.request.core.service.ParallelRequestHandler;

@RequiredArgsConstructor
@Component
public class AccountStatusChangedEventListener {

    private final ParallelRequestHandler handler;

    @EventListener(AccountStatusChangedEvent.class)
    public void onAccountStatusChangedEvent(AccountStatusChangedEvent event) {
        handler.handleParallelRequests(event.getAccountId(), event.getStatus());
    }
}
