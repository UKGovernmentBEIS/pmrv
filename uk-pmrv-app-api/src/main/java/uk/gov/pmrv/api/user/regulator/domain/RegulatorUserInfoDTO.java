package uk.gov.pmrv.api.user.regulator.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegulatorUserInfoDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private String jobTitle;
}
