package uk.gov.pmrv.api.authorization.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "au_authority_permission",
    uniqueConstraints =
    @UniqueConstraint(columnNames = {"authority_id", "permission"}))
public class AuthorityPermission implements Serializable {

    @Id
    @SequenceGenerator(name = "au_authority_permission_id_generator", sequenceName = "au_authority_permission_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_authority_permission_id_generator")
    private Long id;

    @EqualsAndHashCode.Include
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authority_id")
    private Authority authority;

    @EqualsAndHashCode.Include
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Permission permission;
}
