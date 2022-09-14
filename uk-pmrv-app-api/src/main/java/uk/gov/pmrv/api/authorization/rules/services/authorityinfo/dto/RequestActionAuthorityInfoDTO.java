package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestActionAuthorityInfoDTO {

    private Long id;
    private String type;
    
    private ResourceAuthorityInfo authorityInfo;
}
