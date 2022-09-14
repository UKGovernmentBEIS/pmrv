package uk.gov.pmrv.api.workflow.request.application.taskview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfoDTO {

    private String id;
    private RequestType type;
    private CompetentAuthority competentAuthority;
    private Long accountId;
    private RequestMetadata requestMetadata;
}
