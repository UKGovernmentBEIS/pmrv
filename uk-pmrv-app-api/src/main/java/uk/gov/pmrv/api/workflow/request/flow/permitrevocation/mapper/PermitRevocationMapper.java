package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.mapper;

import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PermitRevocationMapper {

    @Mapping(target = "payloadType", expression = "java(uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD)")
    @Mapping(target = "feeAmount", source = "feeAmount")
    PermitRevocationApplicationSubmitRequestTaskPayload toApplicationSubmitRequestTaskPayload(
        PermitRevocationRequestPayload requestPayload,
        BigDecimal feeAmount);
}
