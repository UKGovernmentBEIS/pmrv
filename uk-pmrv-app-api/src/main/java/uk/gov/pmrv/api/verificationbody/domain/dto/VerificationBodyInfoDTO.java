package uk.gov.pmrv.api.verificationbody.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationBodyInfoDTO {

    private Long id;

    private String name;

    private VerificationBodyStatus status;
}
