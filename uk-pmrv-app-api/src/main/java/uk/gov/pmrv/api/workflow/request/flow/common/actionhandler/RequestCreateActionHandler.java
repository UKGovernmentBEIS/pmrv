package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

public interface RequestCreateActionHandler<T extends RequestCreateActionPayload> {

    @Transactional
    String process(Long accountId, RequestCreateActionType type, T payload, PmrvUser pmrvUser);

    RequestCreateActionType getType();
}
