package uk.gov.pmrv.api.migration.permit.managementprocedures;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsManagementProceduresRowMapper implements RowMapper<EtsManagementProceduresDefinition> {

    @Override
    public EtsManagementProceduresDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsManagementProceduresDefinition.builder()
            .emitterId(rs.getString("emitterId"))
            .procedureTitle(rs.getString("procedureTitle"))
            .procedureReference(rs.getString("procedureReference"))
            .diagramReference(rs.getString("diagramReference"))
            .procedureDescription(rs.getString("procedureDescription"))
            .responsiblePostOrDepartment(rs.getString("responsiblePostOrDepartment"))
            .procedureLocation(rs.getString("procedureLocation"))
            .procedureItSystem(rs.getString("procedureItSystem"))
            .enOrOtherStandardsApplied(rs.getString("enOrOtherStandardsApplied"))
            .build();
    }

}
