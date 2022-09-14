package uk.gov.pmrv.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "account_identifier")
@NamedQuery(
        name = AccountIdentifier.NAMED_QUERY_FIND_ACCOUNT_IDENTIFIER,
        query = "select aci "
                + "from AccountIdentifier aci "
                + "where aci.id = 1")
public class AccountIdentifier {

    public static final String NAMED_QUERY_FIND_ACCOUNT_IDENTIFIER = "AccountIdentifier.findAccountIdentifier";

    @Id
    private Integer id;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;
}
