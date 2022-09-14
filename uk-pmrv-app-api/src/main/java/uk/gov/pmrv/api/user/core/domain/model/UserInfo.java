package uk.gov.pmrv.api.user.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
}
