package uk.gov.pmrv.api.workflow.request.flow.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PeerReviewMapper {

    @Mapping(target = "payloadType", source = "payloadType")
    PeerReviewDecisionSubmittedRequestActionPayload toPeerReviewDecisionSubmittedRequestActionPayload(
        PeerReviewDecisionRequestTaskActionPayload taskActionPayload, RequestActionPayloadType payloadType);
}
