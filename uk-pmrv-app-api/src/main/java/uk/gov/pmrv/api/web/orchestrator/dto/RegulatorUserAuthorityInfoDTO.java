package uk.gov.pmrv.api.web.orchestrator.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;

import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RegulatorUserAuthorityInfoDTO {

    private String userId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private LocalDateTime authorityCreationDate;
    private AuthorityStatus authorityStatus;
    private Boolean locked;
}