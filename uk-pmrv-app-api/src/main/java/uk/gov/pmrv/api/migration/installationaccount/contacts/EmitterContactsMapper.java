package uk.gov.pmrv.api.migration.installationaccount.contacts;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmitterContactsMapper implements RowMapper<EmitterContacts> {

    @Override
    public EmitterContacts mapRow(ResultSet rs, int i) throws SQLException {
        return EmitterContacts.builder()
                .emitterId(rs.getString("account_id"))
                .emitterName(rs.getString("name"))
                .emitterDisplayId(rs.getString("emitter_display_id"))
                .primaryContactId(rs.getString("primary_contact_id"))
                .primaryContactEmail(rs.getString("primary_contact_email"))
                .secondaryContactId(rs.getString("secondary_contact_id"))
                .secondaryContactEmail(rs.getString("secondary_contact_email"))
                .serviceContactId(rs.getString("service_contact_id"))
                .serviceContactEmail(rs.getString("service_contact_email"))
                .financeContactId(rs.getString("finance_contact_id"))
                .financeContactEmail(rs.getString("finance_contact_email"))
                .build();
    }
}
