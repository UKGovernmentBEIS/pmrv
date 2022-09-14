package uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.jdbc.core.RowMapper;

public class EtsCalculationSourceStreamCategoryRowMapper implements RowMapper<EtsCalculationSourceStreamCategory> {

    @Override
    public EtsCalculationSourceStreamCategory mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsCalculationSourceStreamCategory.builder()

            .tierId(rs.getString("tier_id"))
            .etsAccountId(rs.getString("fldEmitterID"))
            .sourceStream(rs.getString("source_stream_ref"))
            .emissionSources(Stream.of(rs.getString("emission_source_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .estimatedEmission(rs.getString("estimated_emission"))
            .calculationMethod(rs.getString("monitoring_approach"))
            .sourceStreamCategory(rs.getString("source_category"))
            
            .tierJustification(rs.getString("justification"))
            .measurementDevices(Stream.of(rs.getString("measurement_device_refs").split("\\s*,\\s*"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet()))
            .meteringUncertainty(rs.getString("overall_metering_uncertainty"))
            .activityDataTierApplied(rs.getString("ad_tier_applied"))
            .activityDataIsMaxTier(rs.getBoolean("ad_is_maximum_tier"))
            .activityDataIsHighestTier("yes".equalsIgnoreCase(rs.getString("ad_highest_tier_applied")))
            
            .netCalorificValueTierApplied(rs.getString("ncv_tier_applied"))
            .netCalorificValueIsMaxTier(rs.getBoolean("ncv_is_maximum_tier"))
            .netCalorificValueIsHighestTier("yes".equalsIgnoreCase(rs.getString("ncv_highest_tier_applied")))
            
            .emissionFactorTierApplied(rs.getString("ef_tier_applied"))
            .emissionFactorIsMaxTier(rs.getBoolean("ef_is_maximum_tier"))
            .emissionFactorIsHighestTierApplied("yes".equalsIgnoreCase(rs.getString("ef_highest_tier_applied")))
            
            .oxidationFactorTierApplied(rs.getString("oxf_tier_applied"))
            .oxidationFactorIsMaxTier(rs.getBoolean("oxf_is_maximum_tier"))
            .oxidationFactorIsHighestTierApplied("yes".equalsIgnoreCase(rs.getString("oxf_highest_tier_applied")))
            
            .carbonContentTierApplied(rs.getString("cc_tier_applied"))
            .carbonContentIsMaxTier(rs.getBoolean("cc_is_maximum_tier"))
            .carbonContentIsHighestTierApplied("yes".equalsIgnoreCase(rs.getString("cc_highest_tier_applied")))
            
            .conversionFactorTierApplied(rs.getString("cf_tier_applied"))
            .conversionFactorIsMaxTier(rs.getBoolean("cf_is_maximum_tier"))
            .conversionFactorIsHighestTierApplied("yes".equalsIgnoreCase(rs.getString("cf_highest_tier_applied")))
            
            .biomassFractionTierApplied(rs.getString("bf_tier_applied"))
            .biomassFractionIsMaxTier(rs.getBoolean("bf_is_maximum_tier"))
            .biomassFractionIsHighestTierApplied("yes".equalsIgnoreCase(rs.getString("bf_highest_tier_applied")))

            .build();
    }
}