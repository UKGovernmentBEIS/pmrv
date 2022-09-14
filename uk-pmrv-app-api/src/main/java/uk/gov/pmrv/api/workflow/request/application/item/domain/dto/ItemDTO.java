package uk.gov.pmrv.api.workflow.request.application.item.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    /** item fields **/
    private LocalDateTime creationDate;

    /** request fields **/
    private String requestId;

    private RequestType requestType;

    /** request task fields **/
    private Long taskId;

    @JsonUnwrapped
    private ItemAssigneeDTO itemAssignee;

    private RequestTaskType taskType;

    private Long daysRemaining;

    /** account fields **/
    @JsonUnwrapped
    private ItemAccountDTO account;

    @JsonProperty("isNew")
    private boolean isNew;
    
    private String permitId;
}
