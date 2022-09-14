package uk.gov.pmrv.api.migration.workflow;

import org.springframework.jdbc.core.RowMapper;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkflowEntityVOMapper implements RowMapper<WorkflowEntityVO> {

    @Override
    public WorkflowEntityVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return WorkflowEntityVO.builder()
                .emitterId(rs.getString("fldEmitterID"))
                .workflowId(rs.getString("WorkflowId"))
                .creationDate(rs.getTimestamp("fldDateCreated") != null
                        ? rs.getTimestamp("fldDateCreated").toLocalDateTime()
                        : null)
                .type(RequestType.valueOf(rs.getString("wf_type_pmrv")))
                .status(RequestStatus.valueOf(rs.getString("wf_status_pmrv")))
                .build();
    }
}
