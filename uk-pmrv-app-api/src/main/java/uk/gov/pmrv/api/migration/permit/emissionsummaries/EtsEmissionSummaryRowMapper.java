package uk.gov.pmrv.api.migration.permit.emissionsummaries;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsEmissionSummaryRowMapper implements RowMapper<EtsEmissionSummary> {

    @Override
    public EtsEmissionSummary mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return EtsEmissionSummary.builder()
                .etsAccountId(resultSet.getString("emitterId"))
                .emitterDisplayId(resultSet.getString("emitter_display_id"))
                .sourceStream(ObjectUtils.isEmpty(resultSet.getString("sourceStreamRef")) ? null: resultSet.getString("sourceStreamRef").trim())
                .emissionSources(Stream.of(resultSet.getString("emissionSourceRefs").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toSet()))
                .emissionPoints(Stream.of(
                        resultSet.getString("emissionPointRefs").split("\\s*,\\s*"))
                        .filter(s1 -> !s1.isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toSet()))
                .regulatedActivity(ObjectUtils.isEmpty(resultSet.getString("regulatedActivityRef")) ? null : resultSet.getString("regulatedActivityRef"))
                .excludedRegulatedActivity(resultSet.getBoolean("isExcludedRegulatedActivity"))
                .build();
    }
}
