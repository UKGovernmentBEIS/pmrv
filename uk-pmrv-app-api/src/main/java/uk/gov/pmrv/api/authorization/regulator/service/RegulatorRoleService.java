package uk.gov.pmrv.api.authorization.regulator.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorRolePermissionsDTO;
import uk.gov.pmrv.api.authorization.regulator.transform.RegulatorRoleMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegulatorRoleService {
    private static final RegulatorRoleMapper regulatorRoleMapper = Mappers.getMapper(RegulatorRoleMapper.class);
    private final RoleService roleService;

    /**
     * Returns regulator roles.
     * @return List of RolePermissionsDTO
     */
    public List<RegulatorRolePermissionsDTO> getRegulatorRoles() {
        return roleService.getRegulatorRoles()
                .stream().map(regulatorRoleMapper::toRolePermissionsDTO)
                .collect(Collectors.toList());
    }
}
