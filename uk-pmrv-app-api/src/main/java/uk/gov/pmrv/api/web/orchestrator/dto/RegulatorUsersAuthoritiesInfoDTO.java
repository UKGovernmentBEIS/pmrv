package uk.gov.pmrv.api.web.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RegulatorUsersAuthoritiesInfoDTO {

    @Builder.Default
    private List<RegulatorUserAuthorityInfoDTO> caUsers = new ArrayList<>();
    private boolean editable;
}