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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_payment_bank_account_details")
public class BankAccountDetails {

    @Id
    @SequenceGenerator(name = "request_payment_bank_account_details_id_generator", sequenceName = "request_payment_bank_account_details_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_payment_bank_account_details_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority", unique = true)
    private CompetentAuthority competentAuthority;

    @NotNull
    @Column(name = "sort_code")
    private String sortCode;

    @NotNull
    @Column(name = "account_number")
    private String accountNumber;

    @NotNull
    @Column(name = "account_name")
    private String accountName;

    @Column(name = "iban")
    private String iban;

    @Column(name = "swift_code")
    private String swiftCode;
}
