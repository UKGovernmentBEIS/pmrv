package uk.gov.pmrv.api.workflow.request.core.domain.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.pmrv.api.common.domain.converter.AbstractJsonColumnConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

public class RequestTaskPayloadToJsonConverter extends AbstractJsonColumnConverter<RequestTaskPayload> {

    protected RequestTaskPayloadToJsonConverter(ObjectMapper mapper) {
        super(mapper);
    }
}
