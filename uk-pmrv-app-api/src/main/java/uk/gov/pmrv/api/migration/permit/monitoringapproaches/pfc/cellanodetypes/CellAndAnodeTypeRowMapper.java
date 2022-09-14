package uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.cellanodetypes;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CellAndAnodeTypeRowMapper implements RowMapper<EtsCellAndAnodeType>{

    @Override
    public EtsCellAndAnodeType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsCellAndAnodeType.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .anodeType(rs.getString("Mpfc_anode_type"))
                .cellType(rs.getString("Mpfc_cell_type"))
                .build();
    }

}