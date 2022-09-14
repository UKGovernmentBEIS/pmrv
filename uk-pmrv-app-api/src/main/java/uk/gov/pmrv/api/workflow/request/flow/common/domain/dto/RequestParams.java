package uk.gov.pmrv.api.workflow.request.flow.common.domain.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestParams {

    private RequestType type;
    private CompetentAuthority ca;
    private Long accountId;
    private Long verificationBodyId;
    private RequestPayload requestPayload;
    private RequestMetadata requestMetadata;
    @Builder.Default
    private Map<String, Object> processVars = new HashMap<>();

    @With
    private String requestId;

}
