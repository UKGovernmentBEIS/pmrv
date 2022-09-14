package uk.gov.pmrv.api.workflow.request.flow.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestCreateActionPayload;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class RequestCreateActionEmptyPayload extends RequestCreateActionPayload {

}
