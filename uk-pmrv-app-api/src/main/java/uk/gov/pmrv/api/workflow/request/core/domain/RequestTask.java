package uk.gov.pmrv.api.workflow.request.core.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.workflow.request.core.domain.converter.RequestTaskPayloadToJsonConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

/**
 * The request task entity that represents a workflow task.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_task")
@NamedQuery(
    name = RequestTask.NAMED_QUERY_FIND_REQUEST_TASK_BY_ID,
    query = "select rt "
        + "from RequestTask rt "
        + "where rt.id = :id"
)
@NamedEntityGraph(
    name = RequestTask.NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST,
    attributeNodes = {
        @NamedAttributeNode("request")
    }
)
public class RequestTask {

    public static final String NAMED_QUERY_FIND_REQUEST_TASK_BY_ID = "RequestTask.findRequestTaskById";
    public static final String NAMED_ENTITY_GRAPH_REQUEST_TASK_REQUEST = "RequestTask.request-task-request-graph";

    @Id
    @SequenceGenerator(name = "request_task_id_generator", sequenceName = "request_task_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_task_id_generator")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    @EqualsAndHashCode.Include()
    @NotNull
    @Column(name = "process_task_id", unique = true)
    private String processTaskId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RequestTaskType type;

    @Column(name = "assignee")
    private String assignee;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "pause_date")
    private LocalDate pauseDate;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "payload")
    @Convert(converter = RequestTaskPayloadToJsonConverter.class)
    private RequestTaskPayload payload;

    @Version
    @Setter(AccessLevel.NONE)
    private long version;

    @PreRemove
    public void removeFromRequest() {
        this.getRequest().removeRequestTask(this);
    }
}
