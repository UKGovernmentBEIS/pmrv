package uk.gov.pmrv.api.migration.permit.envpermitsandlicences;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EnvironmentalPermitsAndLicencesSectionMigrationService implements PermitSectionMigrationService<EnvironmentalPermitsAndLicences>{
    
    private final JdbcTemplate migrationJdbcTemplate;
    private static final EnvPermitOrLicenceMapper envPermitOrLicenceMapper = Mappers.getMapper(EnvPermitOrLicenceMapper.class);
    
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
            "T.c.query('Ad_permit_number').value('.', 'NVARCHAR(MAX)') AS permitNumber, \r\n" +
            "T.c.query('Ad_permit_type').value('.', 'NVARCHAR(MAX)') AS permitType, \r\n" +
            "T.c.query('Ad_permit_holder').value('.', 'NVARCHAR(MAX)') AS permitHolder, \r\n" +
            "T.c.query('Ad_competent_body').value('.', 'NVARCHAR(MAX)') AS competentBody \r\n" +
            "from tblEmitter E \r\n" +
            "join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live' \r\n" +
            "join latestPermit F       on E.fldEmitterID = F.fldEmitterID \r\n" +
            "OUTER APPLY f.fldSubmittedXML.nodes('fd:formdata/fielddata/Ad_environmental_licences_list/row') T(c) \r\n"
            ;
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {
        Map<String, EnvironmentalPermitsAndLicences> sections = 
                queryEtsSection(new ArrayList<>(accountsToMigratePermit.keySet()));
        
        sections
            .forEach((etsAccId, section) -> 
                permits.get(accountsToMigratePermit.get(etsAccId).getId())
                    .getPermitContainer().getPermit().setEnvironmentalPermitsAndLicences(section));
    }
    
    
    @Override
    public Map<String, EnvironmentalPermitsAndLicences> queryEtsSection(List<String> accountIds) {
        //construct query
        String query = constructEtsSectionQuery(QUERY_BASE, accountIds);
        
        Map<String, EnvironmentalPermitsAndLicences> accountSections = new HashMap<>();
        
        //execute query
        Map<String, List<EnvPermitOrLicence>> accountEnvPermitOrLicences = executeQuery(query, accountIds);
        for(Entry<String, List<EnvPermitOrLicence>> entry :accountEnvPermitOrLicences.entrySet()) {
            String etsAccountId = entry.getKey();
            List<EnvPermitOrLicence> etsEnvPermitOrLicences = entry.getValue();
            EnvironmentalPermitsAndLicences section = new EnvironmentalPermitsAndLicences();
            if(existEnvPermitAndLicences(entry.getValue())) {
                section.setExist(true);
                section.setEnvPermitOrLicences(envPermitOrLicenceMapper.toPermitEnvPermitOrLicences(etsEnvPermitOrLicences));
            }else {
                section.setExist(false);
            }
            accountSections.put(etsAccountId, section);
        }
        
        return accountSections;
    }

    private Map<String, List<EnvPermitOrLicence>> executeQuery(String query, List<String> accountIds) {
        List<EnvPermitOrLicence> accountEnvPermitOrLicences = migrationJdbcTemplate.query(query,
                new EnvPermitOrLicenceRowMapper(),
                accountIds.isEmpty() ? new Object[] {} : accountIds.toArray());
        return accountEnvPermitOrLicences
                .stream()
                .collect(Collectors.groupingBy(EnvPermitOrLicence::getEtsAccountId));
    }

    private boolean existEnvPermitAndLicences(List<EnvPermitOrLicence> etsEnvPermitOrLicences) {
        if(etsEnvPermitOrLicences.isEmpty()) {
            return false;
        }
        
        if(etsEnvPermitOrLicences.size() == 1) {
            EnvPermitOrLicence etsEnvPermitOrLicence = etsEnvPermitOrLicences.get(0);
            if(etsEnvPermitOrLicence.getPermitType() == null &&
                    etsEnvPermitOrLicence.getPermitNumber() == null && 
                    etsEnvPermitOrLicence.getPermitHolder() == null && 
                    etsEnvPermitOrLicence.getIssuingAuthority() == null) {
                return false;
            }
        }
        
        return true;
    }

}
