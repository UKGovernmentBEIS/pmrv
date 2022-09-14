package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadataRfiable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationRequestMetadata extends RequestMetadata implements RequestMetadataRfiable {

    @Builder.Default
    private List<LocalDateTime> rfiResponseDates = new ArrayList<>();
}
