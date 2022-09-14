package uk.gov.pmrv.api.workflow.request.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_sequence")
@ToString
public class RequestSequence {

    @Id
    @SequenceGenerator(name = "request_sequence_id_generator", sequenceName = "request_sequence_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_sequence_id_generator")
    private Long id;

    @Version
    @Setter(AccessLevel.NONE)
    private long version;

    @EqualsAndHashCode.Include()
    @Column(name = "account_id")
    private Long accountId;

    @EqualsAndHashCode.Include()
    @Enumerated(EnumType.STRING)
    private RequestType type;

    private Long sequence = 0L;

    public RequestSequence(Long accountId, RequestType type) {
        this.accountId = accountId;
        this.type = type;
    }

    public Long incrementSequenceAndGet() {
        this.sequence++;
        return this.sequence;
    }
}
