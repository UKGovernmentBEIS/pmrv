package uk.gov.pmrv.api.account.transform;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Range;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.InstallationCategory;

@UtilityClass
public class InstallationCategoryMapper {

    private static final Map<InstallationCategory, Range<BigDecimal>> categoryRanges =
        new EnumMap<>(InstallationCategory.class);

    static {
        categoryRanges.put(InstallationCategory.A_LOW_EMITTER,
            Range.from(Range.Bound.inclusive(BigDecimal.ZERO)).to(Range.Bound.inclusive(BigDecimal.valueOf(25000))));
        categoryRanges.put(InstallationCategory.A,
            Range.from(Range.Bound.inclusive(BigDecimal.valueOf(25000.1)))
                .to(Range.Bound.inclusive(BigDecimal.valueOf(50000))));
        categoryRanges.put(InstallationCategory.B,
            Range.from(Range.Bound.inclusive(BigDecimal.valueOf(50000.1)))
                .to(Range.Bound.inclusive(BigDecimal.valueOf(500000))));
        categoryRanges.put(InstallationCategory.C,
            Range.from(Range.Bound.inclusive(BigDecimal.valueOf(500000.1))).to(
                Range.Bound.unbounded()));
    }

    public InstallationCategory getInstallationCategory(EmitterType emitterType, BigDecimal estimatedAnnualEmissions) {
        return emitterType == EmitterType.HSE
            ? InstallationCategory.N_A
            : categoryRanges.entrySet().stream()
            .filter(entry -> entry.getValue().contains(estimatedAnnualEmissions))
            .findFirst()
            .map(Map.Entry::getKey)
            .orElse(InstallationCategory.N_A);
    }
}
