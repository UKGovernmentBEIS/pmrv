package uk.gov.pmrv.api.migration.permit.activationdate;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtsActivationDate {

    private String etsAccountId;
    private LocalDate activationDate;
}
