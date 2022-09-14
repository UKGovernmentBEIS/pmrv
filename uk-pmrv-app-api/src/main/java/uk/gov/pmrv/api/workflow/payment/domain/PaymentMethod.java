package uk.gov.pmrv.api.workflow.payment.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_payment_method")
public class PaymentMethod {

    @Id
    @SequenceGenerator(name = "request_payment_method_id_generator", sequenceName = "request_payment_method_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_payment_method_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    private CompetentAuthority competentAuthority;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PaymentMethodType type;
}
