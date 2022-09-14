package uk.gov.pmrv.api.user.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetails {

    private String id;
    private FileInfoDTO signature;

}
