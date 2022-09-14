package uk.gov.pmrv.api.migration.files;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsFileAttachment {
    
    private String etsAccountId;
    private String uploadedFileName;
    private String storedFileName;
    private UUID uuid;
}
