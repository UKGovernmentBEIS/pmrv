package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssigneeUserInfoDTO {

    private String id;

    private String firstName;

    private String lastName;
}
