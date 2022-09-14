package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestActionInfoDTO {

    private Long id;
    private RequestActionType type;
    private String submitter;
    private LocalDateTime creationDate;
}
