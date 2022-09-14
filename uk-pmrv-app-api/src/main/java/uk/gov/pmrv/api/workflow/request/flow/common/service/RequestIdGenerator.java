package uk.gov.pmrv.api.workflow.request.flow.common.service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

/**
 * Generates request id according to the RequestType.
 */
public interface RequestIdGenerator {

    String generate(RequestParams params);

    RequestType getType();

    /**
     * Prefix used when geenerating id.
     * @return
     */
    String getPrefix();
}
