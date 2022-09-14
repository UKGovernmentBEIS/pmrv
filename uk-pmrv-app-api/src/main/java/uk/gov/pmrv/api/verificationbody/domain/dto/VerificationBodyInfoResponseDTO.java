package uk.gov.pmrv.api.verificationbody.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationBodyInfoResponseDTO {

    private List<VerificationBodyInfoDTO> verificationBodies;

    private boolean editable;
}
