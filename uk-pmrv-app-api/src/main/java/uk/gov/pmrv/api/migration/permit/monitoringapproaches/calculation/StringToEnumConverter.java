package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.permit.domain.common.SourceStreamCategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationBiomassFractionTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationCarbonContentTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationConversionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationOxidationFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeteringUncertainty;

@UtilityClass
public class StringToEnumConverter {

    public SourceStreamCategoryType sourceStreamCategoryType(final String sourceStreamCategoryType) {

        switch (sourceStreamCategoryType) {
            case "Major":
                return SourceStreamCategoryType.MAJOR;
            case "Minor":
                return SourceStreamCategoryType.MINOR;
            case "De-minimis":
                return SourceStreamCategoryType.DE_MINIMIS;
            case "Marginal":
                return SourceStreamCategoryType.MARGINAL;
            default:
                return null;
        }
    }

    public CalculationMethod calculationMethod(final String calculationMethod) {

        switch (calculationMethod) {
            case "Standard":
                return CalculationMethod.STANDARD;
            case "Mass Balance":
                return CalculationMethod.MASS_BALANCE;
            default:
                return null;
        }
    }
    
    public MeteringUncertainty meteringUncertainty(final String meteringUncertainty) {

        switch (meteringUncertainty) {
            case "<1.5%":
                return MeteringUncertainty.LESS_OR_EQUAL_1_5;
            case "<2.5%":
                return MeteringUncertainty.LESS_OR_EQUAL_2_5;
            case "<5.0%":
                return MeteringUncertainty.LESS_OR_EQUAL_5_0;
            case "<7.5%":
                return MeteringUncertainty.LESS_OR_EQUAL_7_5;
            case "<10.0%":
                return MeteringUncertainty.LESS_OR_EQUAL_10_0;
            case "<12.5%":
                return MeteringUncertainty.LESS_OR_EQUAL_12_5;
            case "<15.0%":
                return MeteringUncertainty.LESS_OR_EQUAL_15_0;
            case "<17.5%":
                return MeteringUncertainty.LESS_OR_EQUAL_17_5;
            case "N/A":
                return MeteringUncertainty.N_A;
            default:
                return null;
        }
    }

    public CalculationActivityDataTier calculationActivityDataTier(final String calculationActivityDataTier) {

        switch (calculationActivityDataTier) {
            case "No tier":
                return CalculationActivityDataTier.NO_TIER;
            case "1":
                return CalculationActivityDataTier.TIER_1;
            case "2":
                return CalculationActivityDataTier.TIER_2;
            case "3":
                return CalculationActivityDataTier.TIER_3;
            case "4":
                return CalculationActivityDataTier.TIER_4;
            default:
                return null;
        }
    }

    public CalculationNetCalorificValueTier calculationNetCalorificValueTier(final String calculationNetCalorificValueTier) {
        
        if (calculationNetCalorificValueTier == null) return null;
        switch (calculationNetCalorificValueTier) {
            case "No tier":
                return CalculationNetCalorificValueTier.NO_TIER;
            case "1":
                return CalculationNetCalorificValueTier.TIER_1;
            case "2a":
                return CalculationNetCalorificValueTier.TIER_2A;
            case "2b":
                return CalculationNetCalorificValueTier.TIER_2B;
            case "3":
                return CalculationNetCalorificValueTier.TIER_3;
            default:
                return null;
        }
    }

    public CalculationEmissionFactorTier calculationEmissionFactorTier(final String calculationEmissionFactorTier) {

        if (calculationEmissionFactorTier == null) return null;
        switch (calculationEmissionFactorTier) {
            case "No tier":
                return CalculationEmissionFactorTier.NO_TIER;
            case "1":
                return CalculationEmissionFactorTier.TIER_1;
            case "2":
                return CalculationEmissionFactorTier.TIER_2;
            case "2a":
                return CalculationEmissionFactorTier.TIER_2A;
            case "2b":
                return CalculationEmissionFactorTier.TIER_2B;
            case "3":
                return CalculationEmissionFactorTier.TIER_3;
            default:
                return null;
        }
    }

    public CalculationOxidationFactorTier calculationOxidationFactorTier(final String calculationOxidationFactorTier) {

        if (calculationOxidationFactorTier == null) return null;
        switch (calculationOxidationFactorTier) {
            case "No tier":
                return CalculationOxidationFactorTier.NO_TIER;
            case "1":
                return CalculationOxidationFactorTier.TIER_1;
            case "2":
                return CalculationOxidationFactorTier.TIER_2;
            case "3":
                return CalculationOxidationFactorTier.TIER_3;
            default:
                return null;
        }
    }

    public CalculationCarbonContentTier calculationCarbonContentTier(final String calculationCarbonContentTier) {

        if (calculationCarbonContentTier == null) return null;
        switch (calculationCarbonContentTier) {
            case "No tier":
                return CalculationCarbonContentTier.NO_TIER;
            case "1":
                return CalculationCarbonContentTier.TIER_1;
            case "2a":
                return CalculationCarbonContentTier.TIER_2A;
            case "2b":
                return CalculationCarbonContentTier.TIER_2B;
            case "3":
                return CalculationCarbonContentTier.TIER_3;
            default:
                return null;
        }
    }

    public CalculationConversionFactorTier calculationConversionFactorTier(final String calculationConversionFactorTier) {

        if (calculationConversionFactorTier == null) return null;
        switch (calculationConversionFactorTier) {
            case "No tier":
                return CalculationConversionFactorTier.NO_TIER;
            case "1":
                return CalculationConversionFactorTier.TIER_1;
            case "2":
                return CalculationConversionFactorTier.TIER_2;
            default:
                return null;
        }
    }

    public CalculationBiomassFractionTier calculationBiomassFractionTier(final String calculationBiomassFractionTier) {

        if (calculationBiomassFractionTier == null) return null;
        switch (calculationBiomassFractionTier) {
            case "No tier":
                return CalculationBiomassFractionTier.NO_TIER;
            case "1":
                return CalculationBiomassFractionTier.TIER_1;
            case "2":
                return CalculationBiomassFractionTier.TIER_2;
            case "3":
                return CalculationBiomassFractionTier.TIER_3;
            default:
                return null;
        }
    }
    
    public CalculationSamplingFrequency calculationSamplingFrequency(final String calculationSamplingFrequency) {

        if (calculationSamplingFrequency == null) return null;
        switch (calculationSamplingFrequency) {
            case "Continuous":
                return CalculationSamplingFrequency.CONTINUOUS;
            case "Daily":
                return CalculationSamplingFrequency.DAILY;
            case "Weekly":
                return CalculationSamplingFrequency.WEEKLY;
            case "Monthly":
                return CalculationSamplingFrequency.MONTHLY;
            case "Quarterly":
                return CalculationSamplingFrequency.QUARTERLY;
            case "Biannual":
                return CalculationSamplingFrequency.BI_ANNUALLY;
            case "Annual":
                return CalculationSamplingFrequency.ANNUALLY;
            default:
                return null;
        }
    }
}
