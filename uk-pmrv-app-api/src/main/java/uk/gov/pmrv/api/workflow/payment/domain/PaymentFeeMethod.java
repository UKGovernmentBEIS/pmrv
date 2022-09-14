package uk.gov.pmrv.api.workflow.payment.domain;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyEnumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeMethodType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_payment_fee_method")
public class PaymentFeeMethod {

    @Id
    @SequenceGenerator(name = "request_payment_fee_method_id_generator", sequenceName = "request_payment_fee_method_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_payment_fee_method_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    private CompetentAuthority competentAuthority;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FeeMethodType type;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name="type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name="amount")
    @CollectionTable(name = "request_payment_fee", joinColumns = @JoinColumn(name = "fee_method_id"))
    private Map<FeeType, BigDecimal> fees = new EnumMap<>(FeeType.class);
}
