package uk.gov.pmrv.api.migration.permit.confidentialitystatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsConfidentialityStatementRowMapper implements RowMapper<EtsConfidentialityStatement> {
    @Override
    public EtsConfidentialityStatement mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return EtsConfidentialityStatement.builder()
            .etsAccountId(resultSet.getString("emitterId"))
            .exist(resultSet.getBoolean("existConfidentialityStatement"))
            .section(resultSet.getString("section"))
            .justification(resultSet.getString("justification"))
            .build();
    }
}
