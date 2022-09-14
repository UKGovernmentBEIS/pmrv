package uk.gov.pmrv.api.authorization.regulator.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class RegulatorPermissionGroupLevel {

    private final RegulatorPermissionGroup group;

    private final RegulatorPermissionLevel level;
}
