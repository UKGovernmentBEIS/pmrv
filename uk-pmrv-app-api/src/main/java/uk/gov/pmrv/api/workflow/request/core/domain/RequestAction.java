package uk.gov.pmrv.api.workflow.request.core.domain;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.workflow.request.core.domain.converter.RequestActionPayloadToJsonConverter;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "request_action")
public class RequestAction {

    @Id
    @SequenceGenerator(name = "request_action_id_generator", sequenceName = "request_action_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_action_id_generator")
    private Long id;

    @EqualsAndHashCode.Include
    @NotNull
    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @EqualsAndHashCode.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private RequestActionType type;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "payload")
    @Convert(converter = RequestActionPayloadToJsonConverter.class)
    private RequestActionPayload payload;

    @EqualsAndHashCode.Include
    @Column(name = "submitter_id")
    private String submitterId;
    
    @EqualsAndHashCode.Include
    @Column(name = "submitter")
    private String submitter;
    
    @NotNull
    @Column(name = "creation_date")
    @CreatedDate
    private LocalDateTime creationDate;

}
