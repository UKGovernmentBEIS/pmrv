package uk.gov.pmrv.api.workflow.request.application.item.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RequestTaskVisitPK implements Serializable {

    private Long taskId;

    private String userId;
}
