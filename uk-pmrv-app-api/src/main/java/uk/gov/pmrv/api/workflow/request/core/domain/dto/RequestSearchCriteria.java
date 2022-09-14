package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestSearchCriteria {
    
    @NotNull
    private Long accountId;
    
    @Builder.Default
    private Set<RequestType> requestTypes = new HashSet<>();
    
    private RequestStatusCategory status;

    @NotNull
    private RequestCategory category;
    
    @NotNull
    @Min(value = 0, message = "{parameter.page.typeMismatch}")
    private Long page;
    
    @NotNull
    @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
    private Long pageSize;
}
