package uk.gov.pmrv.api.mireport.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExecutedRequestActionsMiReportResult extends MiReportResult {

    private List<ExecutedRequestAction> actions;
}
