package uk.gov.pmrv.api.workflow.request.core.domain.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.common.domain.converter.AbstractJsonColumnConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

public class RequestActionPayloadToJsonConverter extends AbstractJsonColumnConverter<RequestActionPayload> {

	public RequestActionPayloadToJsonConverter(ObjectMapper mapper) {
		super(mapper);
	}

}
