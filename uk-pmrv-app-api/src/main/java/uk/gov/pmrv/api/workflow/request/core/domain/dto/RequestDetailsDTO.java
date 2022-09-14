package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Getter
@EqualsAndHashCode
public class RequestDetailsDTO {

    private String id;
    private RequestType requestType;
    private RequestStatus requestStatus;
    private LocalDate creationDate;
    private RequestMetadata requestMetadata;

    public RequestDetailsDTO(String id, RequestType requestType, RequestStatus requestStatus, LocalDateTime creationDate,
                             RequestMetadata requestMetadata) {
        this.id = id;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.creationDate = creationDate.toLocalDate();
        this.requestMetadata = requestMetadata;
    }
}
