package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestActionDTO {

    private Long id;

    private RequestActionType type;

    private RequestActionPayload payload;

    private String submitter;

    private LocalDateTime creationDate;

    private Long requestAccountId;

}
