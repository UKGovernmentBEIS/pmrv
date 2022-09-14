package uk.gov.pmrv.api.permit.domain.monitoringapproaches.common;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.common.SourceStreamCategoryType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class SourceStreamCategoryBase {

    @NotBlank
    private String sourceStream;

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionSources = new LinkedHashSet<>();

    @NotNull
    @Digits(integer = 8, fraction = 3)
    private BigDecimal annualEmittedCO2Tonnes;

    @NotNull
    private SourceStreamCategoryType categoryType;
}
