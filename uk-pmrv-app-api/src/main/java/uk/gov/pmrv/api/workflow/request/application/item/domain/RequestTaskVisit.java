package uk.gov.pmrv.api.workflow.request.application.item.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(RequestTaskVisitPK.class)
@Table(name = "request_task_visit")
public class RequestTaskVisit {

    @Id
    private Long taskId;

    @Id
    @NotNull
    private String userId;
}
