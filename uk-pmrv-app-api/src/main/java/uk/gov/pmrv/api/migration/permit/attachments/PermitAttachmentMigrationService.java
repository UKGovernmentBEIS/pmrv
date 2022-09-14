package uk.gov.pmrv.api.migration.permit.attachments;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.pmrv.api.files.common.domain.FileStatus;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.files.FileAttachmentMigrationError;
import uk.gov.pmrv.api.migration.files.FileAttachmentMigrationService;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.repository.PermitRepository;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Assumption: <br/>
 * The permits should have already been migrated before migrating their attachments
 */
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class PermitAttachmentMigrationService extends MigrationBaseService {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final FileAttachmentMigrationService fileAttachmentMigrationService;
    private final PermitRepository permitRepository;
    private final AccountRepository accountRepository;

    @Override
    public String getResource() {
        return "permit-attachments";
    }

    // two versions: non-bulk and bulk. Rename method accordingly to choose which will be executed

    // non-bulk migration
    // @Override
    public List<String> migrate(String attachmentUuids) {
        List<String> failedEntries = new ArrayList<>();

        List<String> attachmentUuidList = collectFileAttachmentsUuidToMigrate(attachmentUuids);

        for (String attachmentUuid : attachmentUuidList) {
            fileAttachmentMigrationService.migrateFileAttachment(attachmentUuid)
                .ifPresent(migrationError -> failedEntries.add(buildErrorMessage(migrationError)));
        }

        failedEntries.add("Statistics: Total: " + attachmentUuidList.size() + ". Failed: " + failedEntries.size());
        return failedEntries;
    }

    // bulk migration
    public List<String> migrateBulk(String attachmentUuids) {
        List<String> failedEntries = new ArrayList<>();

        List<String> attachmentUuidList = collectFileAttachmentsUuidToMigrate(attachmentUuids);

        List<FileAttachmentMigrationError> migrationErrors =
            fileAttachmentMigrationService.migrateFileAttachments(attachmentUuidList);
        migrationErrors.forEach(error -> failedEntries.add(buildErrorMessage(error)));

        failedEntries.add("Statistics: Total: " + attachmentUuidList.size() + ". Failed: " + failedEntries.size());
        return failedEntries;
    }

    private String buildErrorMessage(FileAttachmentMigrationError error) {
        if (error.getFileName() == null) {
            return String.format("Attachment UUID: %s | Error message: %s", error.getFileAttachmentUuid(),
                error.getErrorReport());
        }

        String storedEtsFileName = new String(error.getFileContent(), StandardCharsets.UTF_8);
        // print at max 30 characters (for files in pending_migration status, storedName contains the stored filename in ETS server as byte[], 
        // however in general storedName contains the byte[] file content, so to avoid the big print logs, only the first 30 characters are printed)
        String storedEtsFileNamePrinted = storedEtsFileName.substring(0, Math.min(storedEtsFileName.length(), 30));

        String baseErrorMessage = "Attachment UUID: %s | Attachment stored name: %s | Attachment uploaded name: %s | ";
        Optional<PermitEntityAccountDTO> permitAccountOpt =
            permitRepository.findPermitEntityAccountByAttachmentUuid(error.getFileAttachmentUuid());
        if (permitAccountOpt.isEmpty()) {
            return String.format(baseErrorMessage + "Error message: %s",
                error.getFileAttachmentUuid(),
                storedEtsFileNamePrinted,
                error.getFileName(),
                error.getErrorReport()
            );
        } else {
            PermitEntityAccountDTO permitAccountDTO = permitAccountOpt.get();
            Optional<Account> acc = accountRepository.findById(permitAccountDTO.getAccountId());
            return String.format(
                baseErrorMessage + "Permit id: %s | Account id: %s | Ets account id: %s | Error message: %s",
                error.getFileAttachmentUuid(),
                storedEtsFileNamePrinted,
                error.getFileName(),
                permitAccountDTO.getPermitEntityId(),
                acc.isPresent() ? acc.get().getId() : null,
                acc.isPresent() ? acc.get().getMigratedAccountId() : null,
                error.getErrorReport()
            );
        }
    }

    private List<String> collectFileAttachmentsUuidToMigrate(String attachmentUuids) {
        List<String> attachmentUuidList = !StringUtils.isBlank(attachmentUuids)
            ? new ArrayList<>(Arrays.asList(attachmentUuids.split("\\s*,\\s*")))
            : new ArrayList<>();

        if (attachmentUuidList.isEmpty()) {
            attachmentUuidList =
                fileAttachmentRepository.findByStatus(FileStatus.PENDING_MIGRATION)
                    .stream().map(FileAttachment::getUuid).collect(Collectors.toList());
        }
        return attachmentUuidList;
    }

}
