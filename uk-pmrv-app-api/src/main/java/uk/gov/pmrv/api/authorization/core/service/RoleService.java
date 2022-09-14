package uk.gov.pmrv.api.authorization.core.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.RolePermissionsDTO;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.authorization.core.transform.RoleMapper;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private static final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    public Set<String> getCodesByType(RoleType roleType) {
        return roleRepository.findByType(roleType)
                .stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * Returns operator roles.
     * @return List of {@link RoleDTO}
     */
    public List<RoleDTO> getOperatorRoles() {
        return roleRepository.findByType(RoleType.OPERATOR).stream()
                .map(roleMapper::toRoleDTO).collect(Collectors.toList());
    }

    /**
     * Returns regulator roles.
     * @return List of RolePermissionsDTO
     */
    public List<RolePermissionsDTO> getRegulatorRoles() {
        return roleRepository.findByType(REGULATOR).stream()
                .map(roleMapper::toRolePermissionsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the Role that corresponds to the provided role code.
     * @param roleCode the role code
     * @return the {@link RoleDTO}
     */
    public RoleDTO getRoleByCode(String roleCode) {
        Role role = getRoleByRoleCode(roleCode);
        return roleMapper.toRoleDTO(role);
    }

    /**
     * Returns all PMRV roles that correspond to {@link RoleType#VERIFIER}.
     * @return {@link List} of {@link RoleDTO}
     */
    public List<RoleDTO> getVerifierRoleCodes() {
        return roleRepository.findByType(VERIFIER).stream()
                .map(roleMapper::toRoleDTO)
                .collect(Collectors.toList());
    }

    protected Role getRoleByRoleCode(String roleCode) {
        return roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

}
