package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitRevocationApplicationWithdrawRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotBlank
    @Size(max = 10000)
    private String reason;

    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
