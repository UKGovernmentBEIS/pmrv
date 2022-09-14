package uk.gov.pmrv.api.migration.installationaccount;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmitterMapper implements RowMapper<Emitter> {

    @Override
    public Emitter mapRow(ResultSet rs, int i) throws SQLException {
        return Emitter.builder()
                .id(rs.getString("id"))
                .name(rs.getString("name"))
                .emitterDisplayId(rs.getString("emitter_display_id"))
                .siteName(rs.getString("site_name"))
                .competentAuthority(rs.getString("competent_authority"))
                .status(rs.getString("status"))
                .legalEntityId(rs.getString("legal_entity_id"))
                .legalEntityName(rs.getString("legal_entity_name"))
                .locationType(rs.getString("location_type"))
                .gridReference(rs.getString("grid_reference"))
                .acceptedDate(rs.getTimestamp("accepted_date") != null ? rs.getTimestamp("accepted_date").toLocalDateTime() : null)
                .commencementDate(rs.getString("commencement_date"))
                .locationLine1(rs.getString("location_line_1"))
                .locationLine2(rs.getString("location_line_2"))
                .city(rs.getString("location_city"))
                .country(rs.getString("location_country"))
                .postCode(rs.getString("location_postal_code"))
                .longitude(rs.getString("location_longitude"))
                .latitude(rs.getString("location_latitude"))
                .vbId(rs.getString("vb_id"))
                .vbName(rs.getString("vb_name"))
                .sopId(rs.getObject("sop_id") != null ? rs.getLong("sop_id") : null)
                .scheme(rs.getString("scheme"))
                .build();
    }
}
