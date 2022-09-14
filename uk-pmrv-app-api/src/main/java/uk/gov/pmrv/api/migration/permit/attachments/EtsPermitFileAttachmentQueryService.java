package uk.gov.pmrv.api.migration.permit.attachments;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentRowMapper;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentType;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.permit.MigrationPermitHelper.constructEtsSectionQuery;

@Service
@RequiredArgsConstructor
public class EtsPermitFileAttachmentQueryService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final EtsFileAttachmentRowMapper fileAttachmentRowMapper;
    private static final List<String> allowedFileTypes = List.of(".doc", ".docx", ".jpg", ".jpeg", ".pdf", ".png", ".ppt", ".pptx", ".tif", ".txt", ".vsd", ".vsdx", ".xls", ".xlsx");

    private static final String QUERY_BASE =
            "with XMLNAMESPACES (\r\n"
                    + "    'urn:www-toplev-com:officeformsofd' AS fd\r\n"
                    + "), allPermits as (\r\n"
                    + "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, FD.fldDateUpdated, FD.fldFormDataID\r\n"
                    + "    from tblForm F\r\n"
                    + "    join tblFormData FD        on FD.fldFormID = F.fldFormID\r\n"
                    + "    join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n"
                    + "    join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID\r\n"
                    + "    join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID\r\n"
                    + "    where FD.fldMinorVersion = 0 and P.fldDisplayName = 'Phase 3' and FT.fldName = 'IN_PermitApplication'\r\n"
                    + "), mxPVer as (\r\n"
                    + "    select fldFormID 'FormID', max(fldMajorVersion) 'MaxMajorVersion'\r\n"
                    + "    from allPermits\r\n"
                    + "    group by fldFormID\r\n"
                    + "), latestPermit as (\r\n"
                    + "    select e.fldEmitterID, FD.fldSubmittedXML \r\n"
                    + "    from allPermits p\r\n"
                    + "    join mxPVer on p.fldFormID = mxPVer.FormID and p.fldMajorVersion = mxPVer.MaxMajorVersion\r\n"
                    + "    join tblFormData FD on FD.fldFormDataID = p.fldFormDataID\r\n"
                    + "    join tblEmitter E         on E.fldEmitterID = p.fldEmitterID\r\n"
                    + "    join tlkpEmitterStatus ES on E.fldEmitterStatusID = ES.fldEmitterStatusID and ES.fldDisplayName = 'Live'\r\n"
                    + "), emitter_filename as (\r\n"
                    + "    select fldEmitterID, \r\n"
                    + "           T.c.value('File_name[1]', 'nvarchar(max)') attachment_filename\r\n"
                    + "     from latestPermit\r\n"
                    + "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/*[local-name(.)=\"%s\"]/row)') as T(c)\r\n"
                    + "), att as (\r\n"
                    + "    select fldEmitterID, \r\n"
                    + "           T.c.value('(@name)[1]', 'nvarchar(max)') uploaded_filename,\r\n"
                    + "           T.c.value('(@fileName)[1]', 'nvarchar(max)') stored_filename\r\n"
                    + "     from latestPermit\r\n"
                    + "     cross apply fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T(c)\r\n"
                    + ")\r\n"
                    + "select e.fldEmitterID as emitterId, \r\n"
                    + "att.uploaded_filename as uploadedFileName, \r\n"
                    + "att.stored_filename as storedFileName \r\n"
                    + "from emitter_filename e \r\n"
                    + "join att on att.uploaded_filename = e.attachment_filename and att.fldEmitterID = e.fldEmitterID \r\n";

    public Map<String, List<EtsFileAttachment>> query(List<String> accountIds, EtsFileAttachmentType type) {
        return migrationJdbcTemplate
                .query(constructEtsSectionQuery( String.format(QUERY_BASE, type.getDefinition()), accountIds),
                        new ArgumentPreparedStatementSetter(accountIds.isEmpty() ? new Object[] {} : accountIds.toArray()),
                        fileAttachmentRowMapper)
                .stream()
                .filter(etsFileAttachment -> allowedFileTypes.contains(etsFileAttachment.getUploadedFileName().substring(etsFileAttachment.getUploadedFileName().lastIndexOf("."))))
                .collect(Collectors.groupingBy(EtsFileAttachment::getEtsAccountId));
    }
}
