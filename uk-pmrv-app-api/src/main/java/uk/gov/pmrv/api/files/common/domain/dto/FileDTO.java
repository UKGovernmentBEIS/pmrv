package uk.gov.pmrv.api.files.common.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {

    @NotBlank
    private String fileName;
    
    @NotBlank
    private String fileType; // mime type
    
    @NotEmpty
    private byte[] fileContent;
    
    private long fileSize; // bytes
    
}
