package uk.gov.pmrv.api.migration.verifier.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifierUserVO {

    private String roleCode;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String mobileNumber;

    private String verificationBodyId;

    private String verificationBodyName;

    private String userId;
}
