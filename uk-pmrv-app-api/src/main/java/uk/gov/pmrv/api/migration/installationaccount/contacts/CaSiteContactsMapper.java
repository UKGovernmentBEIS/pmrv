package uk.gov.pmrv.api.migration.installationaccount.contacts;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CaSiteContactsMapper implements RowMapper<CaSiteContact> {

    @Override
    public CaSiteContact mapRow(ResultSet rs, int i) throws SQLException {
        return CaSiteContact.builder()
                .emitterId(rs.getString("account_id"))
                .caContactId(rs.getString("ca_contact_id"))
                .emitterName(rs.getString("name"))
                .emitterDisplayId(rs.getString("emitter_display_id"))
                .caContactEmail(rs.getString("ca_contact_email"))
                .build();
    }
}
