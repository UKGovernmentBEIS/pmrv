package uk.gov.pmrv.api.permit.domain.regulatedactivities;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.permit.domain.PermitIdSection;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegulatedActivity extends PermitIdSection {

    @NotNull
    private RegulatedActivityType type;
    
    @Positive
    private BigDecimal capacity;

    @NotNull
    private CapacityUnit capacityUnit;
}
