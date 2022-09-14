package uk.gov.pmrv.api.user.operator.domain;

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
public class OperatorUserInfoDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String phoneNumberCode;
    private String lastLoginDate;
    private String email;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getTelephone() {
        return String.format("+%s%s", phoneNumberCode, phoneNumber);
    }
}
