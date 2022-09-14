package uk.gov.pmrv.api.reporting.domain;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import uk.gov.pmrv.api.reporting.domain.converter.YearAttributeConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.Year;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "aer")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class AerEntity {

    @Id
    private String id;

    @Type(type = "jsonb")
    @Column(name = "data", columnDefinition = "jsonb")
    @Valid
    private AerContainer aerContainer;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @Column(name = "year")
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    private Year year;
}
