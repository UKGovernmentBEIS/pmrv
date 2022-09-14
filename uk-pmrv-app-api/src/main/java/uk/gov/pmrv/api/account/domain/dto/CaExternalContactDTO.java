package uk.gov.pmrv.api.account.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaExternalContactDTO {

    private Long id;

    private String name;

    private String email;

    private String description;

    private LocalDateTime lastUpdatedDate;
}
