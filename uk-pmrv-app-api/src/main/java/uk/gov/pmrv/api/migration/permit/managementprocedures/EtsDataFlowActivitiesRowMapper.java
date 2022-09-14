package uk.gov.pmrv.api.migration.permit.managementprocedures;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EtsDataFlowActivitiesRowMapper implements RowMapper<EtsDataFlowActivities> {

    @Override
    public EtsDataFlowActivities mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsDataFlowActivities.builder()
            .emitterId(rs.getString("emitterId"))
            .procedureTitle(rs.getString("procedureTitle"))
            .procedureReference(rs.getString("procedureReference"))
            .diagramReference(rs.getString("diagramReference"))
            .procedureDescription(rs.getString("procedureDescription"))
            .responsiblePostOrDepartment(rs.getString("responsiblePostOrDepartment"))
            .procedureLocation(rs.getString("procedureLocation"))
            .procedureItSystem(rs.getString("procedureItSystem"))
            .enOrOtherStandardsApplied(rs.getString("enOrOtherStandardsApplied"))
            .primaryDataSources(rs.getString("primaryDataSources"))
            .relevantProcessingSteps(rs.getString("relevantProcessingSteps"))
            .build();
    }

}
