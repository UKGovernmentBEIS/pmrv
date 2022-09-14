package uk.gov.pmrv.api.migration.initialdata;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;
import lombok.Getter;
import lombok.Setter;
import uk.gov.pmrv.api.common.domain.dto.ResourceFile;
import uk.gov.pmrv.api.files.common.domain.FileStatus;

/**
 * Custom implementation to insert initial data in file_document database table using liquibase.
 *
 * The successful run of the changeSet assumes that:
 * -A file exists in the /templates/${competentAuthority}/${fileDocumentName} path under the application resources folder.
 *
 */
@Getter
@Setter
public class InsertDocumentTemplateFileTaskChange extends DocumentTemplateFileUploadTaskChange implements CustomTaskChange {

    private static final String INSERT_DOCUMENT_TEMPLATE_FILE_STATEMENT = "INSERT INTO file_document_template " +
        "(id, uuid, file_name, file_content, file_size, file_type, status, created_by, last_updated_on) " +
        "VALUES (NEXTVAL('file_document_template_seq'),?,?,?,?,?,?,?,?)";

    @Override
    public void execute(Database database) throws CustomChangeException {
        // The context classloader does not include the jar file that contain the resources files,
        // so the code in ResourceFileUtil class trying to find resource using Thread.currentThread() will not work.
        // Solution: set the current class' classloader as the thread's classloader
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        
        ResourceFile documentTemplateFileResource = findCaTemplateResourceFile();
        
        try {
            JdbcConnection conn = (JdbcConnection) database.getConnection();

            PreparedStatement insertFileDocumentStmnt = conn.prepareStatement(INSERT_DOCUMENT_TEMPLATE_FILE_STATEMENT);
            insertFileDocumentStmnt.setString(1, UUID.randomUUID().toString());
            insertFileDocumentStmnt.setString(2, getFileDocumentName());
            insertFileDocumentStmnt.setBytes(3, documentTemplateFileResource.getFileContent());
            insertFileDocumentStmnt.setLong(4, documentTemplateFileResource.getFileSize());
            insertFileDocumentStmnt.setString(5, documentTemplateFileResource.getFileType());
            insertFileDocumentStmnt.setString(6, FileStatus.SUBMITTED.name());
            insertFileDocumentStmnt.setString(7, "system");
            insertFileDocumentStmnt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            insertFileDocumentStmnt.executeUpdate();
        } catch (DatabaseException | SQLException e) {
            throw new CustomChangeException(e.getMessage());
        }
    }
}