package uk.gov.pmrv.api.mireport.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutedRequestAction {

    private Long accountId;

    private AccountType accountType;

    private String accountName;

    private AccountStatus accountStatus;

    private String legalEntityName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permitId;

    private String requestId;

    private RequestType requestType;

    private RequestStatus requestStatus;

    private RequestActionType requestActionType;

    private String requestActionSubmitter;

    private LocalDateTime requestActionCompletionDate;
}
