package uk.gov.pmrv.api.workflow.request.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.converter.RequestMetadataToJsonConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.converter.RequestPayloadToJsonConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "request",
        indexes = {
                @Index(name = "idx_request_process_instance_id", columnList = "process_instance_id", unique = true)
        })
@NamedEntityGraph(name = "fetchRequestActions", attributeNodes = @NamedAttributeNode("requestActions"))
@NamedQuery(
        name = Request.NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_STATUS_AND_TYPE_NOT_NOTIFICATION,
        query = "select r "
            + "from Request r "
            + "where r.accountId = :accountId "
            + "and r.status = :status "
            + "and r.type <> 'SYSTEM_MESSAGE_NOTIFICATION'"
    )
@NamedQuery(
        name = Request.NAMED_QUERY_UPDATE_VERIFICATION_BODY_BY_ACCOUNT_ID,
        query = "update Request "
                + "set verificationBodyId = :verificationBodyId "
                + "where accountId = :accountId ")
public class Request {

    public static final String NAMED_QUERY_FIND_BY_ACCOUNT_ID_AND_STATUS_AND_TYPE_NOT_NOTIFICATION = "Request.findRequestsByAccountIdAndStatusAndTypeNotNotification";
    public static final String NAMED_QUERY_UPDATE_VERIFICATION_BODY_BY_ACCOUNT_ID = "Request.UpdateVerificationBodyByAccountId";

    @Id
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RequestType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @NotNull
    @Column(name = "creation_date")
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    
    /**
     *  Τhe date the first task of the request was completed
     */
    @Column(name = "submission_date")
    private LocalDateTime submissionDate;

    @EqualsAndHashCode.Include()
    @Column(name = "process_instance_id", unique = true)
    private String processInstanceId;

    @Column(name = "competent_authority")
    @Enumerated(EnumType.STRING)
    private CompetentAuthority competentAuthority;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "verification_body_id")
    private Long verificationBodyId;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "payload")
    @Convert(converter = RequestPayloadToJsonConverter.class)
    private RequestPayload payload;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "metadata")
    @Convert(converter = RequestMetadataToJsonConverter.class)
    private RequestMetadata metadata;

    /**
     *  Τhe date the last task of the request was completed
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Builder.Default
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id desc")
    private List<RequestAction> requestActions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestTask> requestTasks = new ArrayList<>();

    public void addRequestAction(RequestAction requestAction) {
        requestAction.setRequest(this);
        this.requestActions.add(requestAction);
    }

    public void addRequestTask(RequestTask requestTask) {
        requestTask.setRequest(this);
        this.requestTasks.add(requestTask);
    }

    public void removeRequestTask(RequestTask requestTask) {
        requestTask.setRequest(null);
        this.getRequestTasks().remove(requestTask);
    }

}
