package uk.gov.pmrv.api.migration.installationaccount;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IMPORTANT NOTE :
 * In order to migrate installation accounts, account identification(mandatory), legal entities (mandatory) and verification bodies (optional)
 * should have been migrated previously.
 */
@Log4j2
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class MigrationInstallationAccountService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final Validator validator;
    private final AccountDTOBuilder accountDTOBuilder;
    private final InstallationAccountCreationService installationAccountCreationService;
    private final InstallationAccountVbAppointService installationAccountVbAppointService;

    private static final String QUERY_BASE  =
            "with XMLNAMESPACES ('urn:www-toplev-com:officeformsofd' AS fd),\r\n" +
                    "allPermits AS (\r\n" +
                    "    SELECT F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID\r\n" +
                    "      FROM tblForm F\r\n" +
                    "      JOIN tblFormData FD ON FD.fldFormID = F.fldFormID\r\n" +
                    "      JOIN tlnkFormTypePhase FTP ON F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n" +
                    "      JOIN tlkpFormType FT ON FTP.fldFormTypeID = FT.fldFormTypeID\r\n" +
                    "      JOIN tlkpPhase P ON FTP.fldPhaseID = P.fldPhaseID\r\n" +
                    "     WHERE FD.fldMinorVersion = 0 AND P.fldDisplayName = 'Phase 3' AND FT.fldName = 'IN_PermitApplication'\r\n" +
                    "),\r\n" +
                    "mxPVer AS (\r\n" +
                    "    SELECT fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion'\r\n" +
                    "      FROM allPermits\r\n" +
                    "     group by fldFormID\r\n" +
                    "),\r\n" +
                    "latestPermit AS (\r\n" +
                    "    SELECT p.fldEmitterID, FD.*\r\n" +
                    "      FROM allPermits p\r\n" +
                    "      JOIN mxPVer ON p.fldFormID = mxPVer.FormID AND p.fldMajorVersion = mxPVer.MaxMajorVersion\r\n" +
                    "      JOIN tblFormData FD ON FD.fldFormDataID = p.fldFormDataID\r\n" +
                    "),\r\n" +
                    "workflowTasks as (\r\n" +
                    "    select wf.fldEmitterID as taskEmitterId, task.fldDateCompleted as taskCompleted, task.fldDateUpdated as taskUpdated\r\n" +
                    "      from tblWorkflow wf inner join tblTask task ON task.fldWorkflowID = wf.fldWorkflowID\r\n" +
                    "     where task.fldName like '%Registration%'\r\n" +
                    ")\r\n" +
                    "select e.fldEmitterID AS id,\r\n" +
                    "       e.fldName AS name,\r\n" +
                    "       e.fldEmitterDisplayId AS emitter_display_id,\r\n" +
                    "       e.fldNapBenchmarkAllowances AS sop_id,\r\n" +
                    "       COALESCE(task.taskCompleted, task.taskUpdated, e.fldDateCreated) as accepted_date,\r\n" +
                    "       inste.fldSiteName as site_name,\r\n" +
                    "       ca.fldName as competent_authority,\r\n" +
                    "       es.fldDisplayName AS status,\r\n" +
                    "       le.fldOperatorID AS legal_entity_id,\r\n" +
                    "       le.fldName AS legal_entity_name,\r\n" +
                    "       addr.fldAddressType AS location_type,\r\n" +
                    "       isnull(f.fldSubmittedXML.value('(fd:formdata/fielddata/Ad_site_main_entrance_grid_reference)[1]', 'NVARCHAR(max)'), '') AS grid_reference,\r\n" +
                    "       isnull(f.fldSubmittedXML.value('(fd:formdata/fielddata/Ad_date_schedule_1_expected)[1]', 'NVARCHAR(max)'), f.fldSubmittedXML.value('(fd:formdata/fielddata/Ad_date_schedule_1_activity_commence)[1]', 'NVARCHAR(max)')) AS commencement_date,\r\n" +
                    "       addr.fldAddressLine1 AS location_line_1,\r\n" +
                    "       addr.fldAddressLine2 AS location_line_2,\r\n" +
                    "       addr.fldCity AS location_city,\r\n" +
                    "       addr.fldCountry AS location_country,\r\n" +
                    "       addr.fldPostcodeZIP AS location_postal_code,\r\n" +
                    "       addr.fldCoordinateEast AS location_longitude,\r\n" +
                    "       addr.fldCoordinateNorth AS location_latitude,\r\n" +
                    "       ver.fldVerifierID AS vb_id,\r\n" +
                    "       ver.fldName AS vb_name,\r\n" +
                    "       case when e.fldEmitterDisplayId in ('12122', '12126', '12129', '12135', '13269') then 'EU_ETS_INSTALLATIONS' else 'UK_ETS_INSTALLATIONS' end scheme\r\n" +
                    "  from tblEmitter e\r\n" +
                    "  join latestPermit f ON e.fldEmitterID = f.fldEmitterID\r\n" +
                    "  join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID\r\n" +
                    "  join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID\r\n" +
                    "  join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = e.fldCompetentAuthorityID\r\n" +
                    "  left join tblOperator le on le.fldOperatorID = e.fldOperatorID\r\n" +
                    "  join tblInstallationEmitter inste on inste.fldEmitterID = e.fldEmitterID\r\n" +
                    "  join tblAddress addr on addr.fldAddressID = inste.fldAddressID\r\n" +
                    "  left join tblVerifier ver on ver.fldVerifierID = e.fldDefaultVerifierID\r\n" +
                    "  left join workflowTasks task on task.taskEmitterId = e.fldEmitterID\r\n" +
                    " where et.fldName = 'Installation' and es.fldDisplayName = 'Live'"
                    + " and e.fldEmitterDisplayId not in (14206)";


    @Override
    public String getResource() {
        return "installation-accounts";
    }

    @Override
    public List<String> migrate(String ids) {
        final String query = InstallationAccountHelper.constructQuery(QUERY_BASE, ids);

        List<String> results = new ArrayList<>();
        List<Emitter> emitters = migrationJdbcTemplate.query(query, new EmitterMapper());

        AtomicInteger failedCounter = new AtomicInteger(0);
        for (Emitter emitter: emitters) {
            List<String> migrationResults = doMigrate(emitter, failedCounter);
            results.addAll(migrationResults);
        }

        results.add("migration of accounts results: " + failedCounter + "/" + emitters.size() + " failed");
        return results;
    }

    private List<String> doMigrate(Emitter emitter, AtomicInteger failedCounter) {
        List<String> results = new ArrayList<>();
        String accountId = emitter.getId();

        AccountDTO accountDTO = accountDTOBuilder.constructAccountDTO(emitter);

        // validate account
        Set<ConstraintViolation<AccountDTO>> constraintViolations = validator.validate(accountDTO);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v ->
                    results.add(InstallationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCompetentAuthority(),
                            v.getMessage(), v.getPropertyPath().iterator().next().getName() + ":" + v.getInvalidValue())));
            failedCounter.incrementAndGet();
            return results;
        }
        
        try {
            // create account
            Long persistedAccountId = installationAccountCreationService.createAccount(accountDTO, emitter);
            
            // appoint vb
           /* Optional.ofNullable(emitter.getVbName())
                .ifPresent(name -> installationAccountVbAppointService.appointVerificationBodyToAccount(persistedAccountId, name));*/
            
            results.add(InstallationAccountHelper.constructSuccessMessage(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCompetentAuthority()));
        } catch (Exception ex) {
            failedCounter.incrementAndGet();
            log.error("migration of installation account: {} failed with {}",
                    emitter.getId(), ExceptionUtils.getRootCause(ex).getMessage());
            results.add(InstallationAccountHelper.constructErrorMessageWithCA(accountId, accountDTO.getName(), emitter.getEmitterDisplayId(), emitter.getCompetentAuthority(),
                    ExceptionUtils.getRootCause(ex).getMessage(), null));
        }
        return results;
    }

}
