package uk.gov.pmrv.api.workflow.request.flow.rfi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RfiMapper {
    
    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType.RFI_SUBMITTED_PAYLOAD)")
    RfiSubmittedRequestActionPayload toRfiSubmittedRequestActionPayload(RfiSubmitRequestTaskActionPayload taskActionPayload);
}
