package uk.gov.pmrv.api.authorization.core.domain.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthoritiesDTO {

    @Builder.Default
    private List<UserAuthorityDTO> authorities = new ArrayList<>();

    /**
     * Whether the user authority properties should be considered as editable
     */
    private boolean editable;
}
