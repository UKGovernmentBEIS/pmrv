package uk.gov.pmrv.api.migration.permit.installationcategory;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EstimatedAnnualEmissionsSectionMigrationService
    implements PermitSectionMigrationService<EstimatedAnnualEmissions> {

    private final JdbcTemplate migrationJdbcTemplate;

    private static final String QUERY_BASE =
        "with \r\n" +
            "   XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd), \r\n" +
            "   allPermits as ( \r\n" +
            "       select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID \r\n" +
            "       from tblForm F \r\n" +
            "       join tblFormData FD on FD.fldFormID = F.fldFormID \r\n" +
            "       join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID \r\n" +
            "       join tlkpFormType FT on FTP.fldFormTypeID = FT.fldFormTypeID \r\n" +
            "       join tlkpPhase P on FTP.fldPhaseID = P.fldPhaseID \r\n" +
            "       where FD.fldMinorVersion = 0 \r\n" +
            "       and P.fldDisplayName = 'Phase 3' \r\n" +
            "       and FT.fldName = 'IN_PermitApplication'), \r\n" +
            "   mxPVer as ( \r\n" +
            "       select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion' \r\n" +
            "       from allPermits \r\n" +
            "       group by fldFormID), \r\n" +
            "   latestPermit as ( \r\n" +
            "       select p.fldEmitterID, FD.* \r\n" +
            "       from allPermits p \r\n" +
            "       join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion \r\n" +
            "       join tblFormData FD on FD.fldFormDataID = p.fldFormDataID) \r\n" +
            "select \r\n" +
            "E.fldEmitterID as emitterId, \r\n" +
            "F.fldSubmittedXML.value('(fd:formdata/fielddata/Ia_estimated_annual_emission)[1]', 'NVARCHAR(max)') AS estimatedAnnualEmission \r\n" +
            "from tblEmitter E \r\n" +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" +
            "join latestPermit F       on E.fldEmitterID = F.fldEmitterID \r\n";

    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, EstimatedAnnualEmissions> sections =
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));

        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setEstimatedAnnualEmissions(section));
    }

    @Override
    public Map<String, EstimatedAnnualEmissions> queryEtsSection(List<String> accountIds) {
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);
        List<EtsEstimatedAnnualEmissions> estEstimatedAnnualEmissions = executeQuery(query, accountIds);

        return estEstimatedAnnualEmissions.stream()
            .collect(Collectors.toMap(EtsEstimatedAnnualEmissions::getEmitterId,
                EstimatedAnnualEmissionsMapper::toEstimatedAnnualEmissions));
    }

    private List<EtsEstimatedAnnualEmissions> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEstimatedAnnualEmissionsRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }
}
