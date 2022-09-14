package uk.gov.pmrv.api.migration.permit.measurementdevices;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MeasurementDevicesOrMethodsMigrationService
    implements PermitSectionMigrationService<MeasurementDevicesOrMethods> {

    private final JdbcTemplate migrationJdbcTemplate;

    private final MeasurementDeviceOrMethodMapper measurementDeviceOrMethodMapper;

    private static final String QUERY_BASE =
        "\t with XMLNAMESPACES (\r\n" +
            "    'urn:www-toplev-com:officeformsofd' AS fd\r\n" +
            "), allPermits as (\r\n" +
            "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID\r\n" +
            "      from tblForm F\r\n" +
            "      join tblFormData FD        on FD.fldFormID = F.fldFormID\r\n" +
            "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n" +
            "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\r\n" +
            "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\r\n" +
            "     where FD.fldMinorVersion = 0 and P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\r\n" +
            "), mxPVer as (\r\n" +
            "    select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion'\r\n" +
            "      from allPermits\r\n" +
            "     group by fldFormID\r\n" +
            "), latestPermit as (\r\n" +
            "    select e.fldEmitterID, FD.*\r\n" +
            "      from allPermits p\r\n" +
            "      join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion\r\n" +
            "      join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\r\n" +
            "      join tblEmitter E on E.fldEmitterID = p.fldEmitterID\r\n" +
            "      join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live'\r\n" +
            "), devices as (\r\n" +
            "    select\r\n" +
            "           f.fldEmitterID as emitterId,\r\n" +
            "           T.c.query('Calc_metering_device_ref').value('.', 'NVARCHAR(MAX)') as reference,\r\n" +
            "           T.c.query('Calc_type_of_metering_device').value('.', 'NVARCHAR(MAX)') as type,\r\n" +
            "           T.c.query('Calc_metering_range').value('.', 'NVARCHAR(MAX)') as range,\r\n" +
            "           T.c.query('Calc_metering_range_units').value('.', 'NVARCHAR(MAX)') as rangeUnits,\r\n" +
            "           T.c.query('Calc_specified_uncertainty').value('.', 'NVARCHAR(MAX)') as uncertainty,\r\n" +
            "           T.c.query('Calc_metering_device_location').value('.', 'NVARCHAR(MAX)') as location\r\n" +
            "      from latestPermit F\r\n" +
            "     cross APPLY fldSubmittedXML.nodes('(fd:formdata/fielddata/Calc_metering_devices/row)') as T(c)\r\n" +
            "    union\r\n" +
            "    select\r\n" +
            "           f.fldEmitterID as emitterId,\r\n" +
            "           T.c.query('Meas_measurement_device_ref').value('.', 'NVARCHAR(MAX)') as reference,\r\n" +
            "           T.c.query('Meas_type_of_measurement_device').value('.', 'NVARCHAR(MAX)') as type,\r\n" +
            "           T.c.query('Meas_measurement_range').value('.', 'NVARCHAR(MAX)') as range,\r\n" +
            "           T.c.query('Meas_measurement_range_units').value('.', 'NVARCHAR(MAX)') as rangeUnits,\r\n" +
            "           T.c.query('Meas_measurement_uncertainty').value('.', 'NVARCHAR(MAX)') as uncertainty,\r\n" +
            "           T.c.query('Meas_measurement_device_location').value('.', 'NVARCHAR(MAX)') as location\r\n" +
            "      from latestPermit F\r\n" +
            "     cross APPLY fldSubmittedXML.nodes('(fd:formdata/fielddata/Meas_measurement_devices/row)') as T(c)\r\n" +
            "    union\r\n" +
            "    select\r\n" +
            "           f.fldEmitterID as emitterId,\r\n" +
            "           T.c.query('Mn2o_measurement_device_ref').value('.', 'NVARCHAR(MAX)') as reference,\r\n" +
            "           T.c.query('Mn2o_type_of_measurement_device').value('.', 'NVARCHAR(MAX)') as type,\r\n" +
            "           T.c.query('Mn2o_measurement_range').value('.', 'NVARCHAR(MAX)') as range,\r\n" +
            "           T.c.query('Mn2o_measurement_range_units').value('.', 'NVARCHAR(MAX)') as rangeUnits,\r\n" +
            "           T.c.query('Mn2o_measurement_uncertainty').value('.', 'NVARCHAR(MAX)') as uncertainty,\r\n" +
            "           T.c.query('Mn2o_measurement_device_location').value('.', 'NVARCHAR(MAX)') as location\r\n" +
            "      from latestPermit F\r\n" +
            "     cross APPLY fldSubmittedXML.nodes('(fd:formdata/fielddata/Mn2o_measurement_devices/row)') as T(c)\r\n" +
            "    union\r\n" +
            "    select\r\n" +
            "           f.fldEmitterID as emitterId,\r\n" +
            "           T.c.query('Mtico2_measurement_device_ref').value('.', 'NVARCHAR(MAX)') as reference,\r\n" +
            "           T.c.query('Mtico2_type_of_measurement_device').value('.', 'NVARCHAR(MAX)') as type,\r\n" +
            "           T.c.query('Mtico2_measurement_range').value('.', 'NVARCHAR(MAX)') as range,\r\n" +
            "           T.c.query('Mtico2_measurement_range_units').value('.', 'NVARCHAR(MAX)') as rangeUnits,\r\n" +
            "           T.c.query('Mtico2_measurement_uncertainty').value('.', 'NVARCHAR(MAX)') as uncertainty,\r\n" +
            "           'N/A' as location\r\n" +
            "      from latestPermit F\r\n" +
            "     cross APPLY fldSubmittedXML.nodes('(fd:formdata/fielddata/Mtico2_measurement_devices/row)') as T(c)\r\n" +
            ") select emitterId, \r\n"
            + "reference, \r\n"
            + "type, \r\n"
            + "COALESCE(NULLIF(range,''), 'N/A')  as range, \r\n"
            + "COALESCE(NULLIF(rangeUnits,''), 'N/A')  as rangeUnits, \r\n"
            + "uncertainty, \r\n"
            + "COALESCE(NULLIF(location,''), 'N/A') as location \r\n"
            + "from devices \r\n";
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, MeasurementDevicesOrMethods> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setMeasurementDevicesOrMethods(section));
    }

    @Override
    public Map<String, MeasurementDevicesOrMethods> queryEtsSection(List<String> accountIds) {
        String query = constructQuery(accountIds);

        Map<String, List<EtsMeteringMeasurementDevice>> accountMeteringMeasurementDevices =
            executeQuery(query, accountIds);

        return accountMeteringMeasurementDevices.entrySet().stream()
            .collect(Collectors
                .toMap(Map.Entry::getKey,
                    entry -> MeasurementDevicesOrMethods.builder()
                        .measurementDevicesOrMethods(measurementDeviceOrMethodMapper.toMeasurementDeviceOrMethodList(entry.getValue()))
                        .build()
                )
            );
    }

    private Map<String, List<EtsMeteringMeasurementDevice>> executeQuery(String query, List<String> accountIds) {
        List<EtsMeteringMeasurementDevice> etsMeteringMeasurementDevices = migrationJdbcTemplate.query(query,
                new EtsMeteringMeasurementDeviceRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return etsMeteringMeasurementDevices
            .stream()
            .collect(Collectors.groupingBy(EtsMeteringMeasurementDevice::getEmitterId));
    }

    private String constructQuery(List<String> accountIds) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        if(!accountIds.isEmpty()) {
            String inAccountIdsSql = String.join(",", Collections.nCopies(accountIds.size(), "?"));
            queryBuilder.append(String.format("where emitterId in (%s)", inAccountIdsSql));
        }
        return queryBuilder.toString();
    }

}
