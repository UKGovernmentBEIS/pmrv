package uk.gov.pmrv.api.authorization.regulator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AuthorityManagePermissionDTO {

    private Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissions;

    private boolean editable;
}
