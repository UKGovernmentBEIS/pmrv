package uk.gov.pmrv.api.account.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "account_search_additional_keyword")
public class AccountSearchAdditionalKeyword {

    @Id
    @SequenceGenerator(name = "account_search_additional_keyword_id_generator", sequenceName = "account_search_additional_keyword_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_search_additional_keyword_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    private Long accountId;

    @EqualsAndHashCode.Include()
    private String value;
}
