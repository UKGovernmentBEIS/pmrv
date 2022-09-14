package uk.gov.pmrv.api.workflow.request.core.domain.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.pmrv.api.common.domain.converter.AbstractJsonColumnConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

public class RequestPayloadToJsonConverter extends AbstractJsonColumnConverter<RequestPayload> {

    protected RequestPayloadToJsonConverter(ObjectMapper mapper) {
        super(mapper);
    }
}
