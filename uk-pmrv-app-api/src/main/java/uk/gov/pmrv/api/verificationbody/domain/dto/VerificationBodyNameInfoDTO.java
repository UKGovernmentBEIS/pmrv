package uk.gov.pmrv.api.verificationbody.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationBodyNameInfoDTO {
    
    private Long id;
    private String name;
}
