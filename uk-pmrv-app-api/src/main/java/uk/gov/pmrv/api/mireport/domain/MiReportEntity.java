package uk.gov.pmrv.api.mireport.domain;

import javax.persistence.*;
import lombok.*;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
    name = "mi_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"competent_authority", "type"})
)
public class MiReportEntity {

    @Id
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MiReportType miReportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    private CompetentAuthority competentAuthority;
}
