package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfiResponsePayload {

    @NotEmpty
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotBlank @Size(max = 10000) String> answers = new ArrayList<>();

    @Builder.Default
    private Set<UUID> files = new HashSet<>();
}
