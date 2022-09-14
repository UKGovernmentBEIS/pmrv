package uk.gov.pmrv.api.workflow.request.core.domain.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.common.domain.converter.AbstractJsonColumnConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;

public class RequestMetadataToJsonConverter extends AbstractJsonColumnConverter<RequestMetadata> {

    protected RequestMetadataToJsonConverter(ObjectMapper mapper) {
        super(mapper);
    }
}
