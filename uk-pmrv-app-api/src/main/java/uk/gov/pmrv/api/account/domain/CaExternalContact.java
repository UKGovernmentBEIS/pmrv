package uk.gov.pmrv.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "account_ca_external_contacts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"competent_authority", "name"}),
        @UniqueConstraint(columnNames = {"competent_authority", "email"})})
public class CaExternalContact {

    @Id
    @SequenceGenerator(name = "ca_external_contacts_generator", sequenceName = "account_ca_external_contacts_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ca_external_contacts_generator")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthority competentAuthority;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "email")
    @NotBlank
    private String email;

    @Column(name = "description")
    @NotBlank
    private String description;

    @NotNull
    @Column(name = "last_updated_date")
    @LastModifiedDate
    private LocalDateTime lastUpdatedDate;
}
