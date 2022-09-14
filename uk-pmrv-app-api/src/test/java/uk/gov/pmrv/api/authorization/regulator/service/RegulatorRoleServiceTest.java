package uk.gov.pmrv.api.authorization.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.RolePermission;
import uk.gov.pmrv.api.authorization.core.domain.dto.RolePermissionsDTO;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorRolePermissionsDTO;
import uk.gov.pmrv.api.authorization.regulator.transform.RegulatorPermissionsAdapter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;

@ExtendWith(MockitoExtension.class)
class RegulatorRoleServiceTest {
    @InjectMocks
    private RegulatorRoleService regulatorRoleService;

    @Mock
    private RoleService roleService;

    @Test
    void getRegulatorRoles() {
        RolePermissionsDTO role1 = RolePermissionsDTO.builder()
                .code("code1")
                .rolePermissions(List.of(RolePermission.builder()
                        .permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK)
                        .build()))
                .build();
        RolePermissionsDTO role2 = RolePermissionsDTO.builder()
                .code("code2")
                .rolePermissions(List.of(RolePermission.builder()
                        .permission(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK).
                                build()))
                .build();

        when(roleService.getRegulatorRoles()).thenReturn(List.of(role1, role2));

        List<RegulatorRolePermissionsDTO> regulatorRolePermissionsDTOS = regulatorRoleService.getRegulatorRoles();
        RegulatorRolePermissionsDTO regulatorRolePermissionsDTO1 = RegulatorRolePermissionsDTO.builder()
                .code("code1")
                .rolePermissions(RegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK)))
                .build();
        RegulatorRolePermissionsDTO regulatorRolePermissionsDTO2 = RegulatorRolePermissionsDTO.builder()
                .code("code2")
                .rolePermissions(RegulatorPermissionsAdapter.getPermissionGroupLevelsFromPermissions(List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK)))
                .build();
        assertThat(regulatorRolePermissionsDTOS).hasSameElementsAs(List.of(regulatorRolePermissionsDTO1, regulatorRolePermissionsDTO2));
    }
}