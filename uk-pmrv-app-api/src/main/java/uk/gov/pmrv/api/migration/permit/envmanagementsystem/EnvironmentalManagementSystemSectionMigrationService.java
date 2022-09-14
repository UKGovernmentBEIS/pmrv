package uk.gov.pmrv.api.migration.permit.envmanagementsystem;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.envmanagementsystem.EnvironmentalManagementSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EnvironmentalManagementSystemSectionMigrationService
        implements PermitSectionMigrationService<EnvironmentalManagementSystem> {

    private final JdbcTemplate migrationJdbcTemplate;
    private static final MigrationEnvironmentalManagementSystemMapper environmentalManagementSystemMapper =
            Mappers.getMapper(MigrationEnvironmentalManagementSystemMapper.class);

    private static final String QUERY_BASE  =
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
                    "case \r\n" +
                    "   when isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_does_organisation_have_ems)[1]', 'NVARCHAR(max)'), '') = 'Yes' then cast (1 as bit) \r\n" +
                    "   else cast(0 as bit) \r\n" +
                    "end as emsExist, \r\n" +
                    "case \r\n" +
                    "   when isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_is_ems_certified)[1]', 'NVARCHAR(max)'), '') = 'Yes' then cast (1 as bit) \r\n" +
                    "   else cast(0 as bit) \r\n" +
                    "end as emsCertified, \r\n" +
                    "isnull(F.fldSubmittedXML.value('(fd:formdata/fielddata/Man_ems_standard)[1]', 'NVARCHAR(max)'), '') AS emsCertificationStandard \r\n" +
                    "from tblEmitter E \r\n" +
                    "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" +
                    "join latestPermit F on E.fldEmitterID = F.fldEmitterID \r\n";

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {
        Map<String, EnvironmentalManagementSystem> sections =
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));

        this.amendEnvironmentalManagementSystems(sections.values());

        sections
                .forEach((etsAccId, section) ->
                        permits.get(accountsToMigratePermit.get(etsAccId).getId())
                                .getPermitContainer().getPermit().getManagementProcedures().setEnvironmentalManagementSystem(section));
    }

    private void amendEnvironmentalManagementSystems(final Collection<EnvironmentalManagementSystem> systems) {

        systems.stream().filter(EnvironmentalManagementSystem::isExist).forEach(s -> {
            if (ObjectUtils.isEmpty(s.getCertificationStandard())) {
                s.setCertified(false);
                s.setCertificationStandard(null);
            } else if (Boolean.FALSE.equals(s.getCertified())) {
                s.setCertificationStandard(null);
            }
        });

        systems.stream().filter(environmentalManagementSystem -> !environmentalManagementSystem.isExist()).forEach(s -> {
            if (Boolean.FALSE.equals(s.getCertified())) {
                s.setCertified(null);
                s.setCertificationStandard(null);
            } else {
                if (ObjectUtils.isEmpty(s.getCertificationStandard())) {
                    s.setCertified(null);
                    s.setCertificationStandard(null);
                } else {
                    s.setExist(true);
                    s.setCertified(true);
                }
            }
        });
    }

    @Override
    public Map<String, EnvironmentalManagementSystem> queryEtsSection(List<String> accountIds) {
        //construct query
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);

        List<EtsEnvironmentalManagementSystem> etsEnvironmentalManagementSystems = executeQuery(query, accountIds);

        return etsEnvironmentalManagementSystems.stream()
                .collect(Collectors.toMap(EtsEnvironmentalManagementSystem::getEtsAccountId,
                        environmentalManagementSystemMapper::toEnvironmentalManagementSystem));

    }

    private List<EtsEnvironmentalManagementSystem> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEnvironmentalManagementSystemRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
    }
}
