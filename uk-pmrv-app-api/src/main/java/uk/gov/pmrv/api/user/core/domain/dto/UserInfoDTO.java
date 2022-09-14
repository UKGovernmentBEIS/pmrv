package uk.gov.pmrv.api.user.core.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class UserInfoDTO {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private AuthenticationStatus status;
    private Boolean locked;
    
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
