package uk.gov.pmrv.api.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResourceFile {

    private String fileType;

    private byte[] fileContent;

    private long fileSize;
}
