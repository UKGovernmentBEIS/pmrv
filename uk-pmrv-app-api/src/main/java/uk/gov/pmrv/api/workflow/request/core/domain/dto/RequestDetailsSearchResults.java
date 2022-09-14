package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDetailsSearchResults {

    private List<RequestDetailsDTO> requestDetails;
    private Long total;
    
}
