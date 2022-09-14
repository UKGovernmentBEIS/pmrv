package uk.gov.pmrv.api.migration.initialdata;

import java.io.File;
import java.io.IOException;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import lombok.Getter;
import lombok.Setter;
import uk.gov.pmrv.api.common.domain.dto.ResourceFile;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.utils.ResourceFileUtil;

@Getter
@Setter
public abstract class DocumentTemplateFileUploadTaskChange implements CustomTaskChange {

	private CompetentAuthority competentAuthority;
    private String fileDocumentName;

    @Override
    public String getConfirmationMessage() {
        return "File document upload successfully completed";
    }

    @Override
    public void setUp() throws SetupException {
    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }

    protected ResourceFile findCaTemplateResourceFile()
        throws CustomChangeException {
        String resourcePath = "templates" + File.separator + "ca" + File.separator + competentAuthority.name().toLowerCase() + File.separator + fileDocumentName;
        try {
            return ResourceFileUtil.getResourceFile(resourcePath);
        } catch (IOException e) {
            throw new CustomChangeException(e.getMessage());
        }
    }
}
