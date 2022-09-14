package uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EtsReceivingTransferringInstallationRowMapper implements RowMapper<EtsReceivingTransferringInstallation> {

    @Override
    public EtsReceivingTransferringInstallation mapRow(ResultSet rs, int rowNum) throws SQLException {
        return EtsReceivingTransferringInstallation.builder()
                .etsAccountId(rs.getString("fldEmitterID"))
                .type(rs.getString("Mtico2_transferring_or_receiving_installation"))
                .installationIdentificationCode(rs.getString("Mtico2_installation_identification_code"))
                .operator(rs.getString("Mtico2_operator"))
                .installationName(rs.getString("Mtico2_installation_name"))
                .co2source(rs.getString("Mtico2_source_of_co2"))
                .build();
    }
}
