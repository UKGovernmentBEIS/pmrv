package uk.gov.pmrv.api.authorization.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityInfoDTO {

	private Long id;
    private String userId;
    private AuthorityStatus authorityStatus;
    private LocalDateTime creationDate;
    private Long accountId;
    private String code;
    private Long verificationBodyId;
}