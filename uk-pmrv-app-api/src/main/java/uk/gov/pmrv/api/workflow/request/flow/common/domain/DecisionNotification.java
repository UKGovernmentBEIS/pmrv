package uk.gov.pmrv.api.workflow.request.flow.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionNotification {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> operators = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<Long> externalContacts = new HashSet<>();

    @NotBlank
    private String signatory;
}
