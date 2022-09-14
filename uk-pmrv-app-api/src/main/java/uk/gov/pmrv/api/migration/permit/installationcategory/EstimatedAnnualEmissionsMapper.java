package uk.gov.pmrv.api.migration.permit.installationcategory;

import java.math.BigDecimal;
import org.apache.commons.lang3.math.NumberUtils;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;

public class EstimatedAnnualEmissionsMapper {

    static EstimatedAnnualEmissions toEstimatedAnnualEmissions(EtsEstimatedAnnualEmissions etsEstimatedAnnualEmissions) {
        EstimatedAnnualEmissions estimatedAnnualEmissions = new EstimatedAnnualEmissions();
        String estimatedAnnualEmissionStr = etsEstimatedAnnualEmissions.getEstimatedAnnualEmission();
        if (NumberUtils.isParsable(estimatedAnnualEmissionStr)) {
            BigDecimal estimatedAnnualEmission = new BigDecimal(estimatedAnnualEmissionStr);
            estimatedAnnualEmissions.setQuantity(estimatedAnnualEmission);
        }
        return estimatedAnnualEmissions;
    }
}
