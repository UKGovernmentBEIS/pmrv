package uk.gov.pmrv.api.authorization.regulator.transform;

import static java.util.stream.Collectors.toList;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.*;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.*;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroupLevel;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel;

@UtilityClass
public class RegulatorPermissionsAdapter {

    private final Map<RegulatorPermissionGroupLevel, List<Permission>> permissionGroupLevelsConfig;

    static {
        permissionGroupLevelsConfig = new LinkedHashMap<>();

        //REVIEW_INSTALLATION_ACCOUNT
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_INSTALLATION_ACCOUNT, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_INSTALLATION_ACCOUNT, VIEW_ONLY),
                List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_INSTALLATION_ACCOUNT, EXECUTE),
                List.of(PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK,
                    PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK));

        //MANAGE_USERS_AND_CONTACTS
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, NONE),
                Collections.emptyList());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(MANAGE_USERS_AND_CONTACTS, EXECUTE),
                List.of(PERM_CA_USERS_EDIT));

        //ADD_OPERATOR_ADMIN
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ADD_OPERATOR_ADMIN, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ADD_OPERATOR_ADMIN, EXECUTE),
                List.of(PERM_ACCOUNT_USERS_EDIT));

        //ASSIGN_REASSIGN TASKS
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(ASSIGN_REASSIGN_TASKS, EXECUTE),
                List.of(PERM_TASK_ASSIGNMENT));

        //MANAGE_VERIFICATION_BODIES
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_VERIFICATION_BODIES, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(MANAGE_VERIFICATION_BODIES, EXECUTE),
                        List.of(PERM_VB_MANAGE));
        
        // REVIEW_PERMIT_APPLICATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_APPLICATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_APPLICATION, VIEW_ONLY),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_APPLICATION, EXECUTE),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_VIEW_TASK,
                        PERM_PERMIT_ISSUANCE_APPLICATION_REVIEW_EXECUTE_TASK));
        
        // PEER_REVIEW_PERMIT_APPLICATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_APPLICATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_APPLICATION, VIEW_ONLY),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_APPLICATION, EXECUTE),
                List.of(PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_VIEW_TASK,
                        PERM_PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_EXECUTE_TASK));
        
        // REVIEW_PERMIT_SURRENDER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_SURRENDER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_SURRENDER, VIEW_ONLY),
                List.of(PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_SURRENDER, EXECUTE),
                List.of(PERM_PERMIT_SURRENDER_REVIEW_VIEW_TASK,
                        PERM_PERMIT_SURRENDER_REVIEW_EXECUTE_TASK));
        
        // PEER_REVIEW_PERMIT_SURRENDER
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_SURRENDER, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_SURRENDER, VIEW_ONLY),
                List.of(PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_SURRENDER, EXECUTE),
                List.of(PERM_PERMIT_SURRENDER_PEER_REVIEW_VIEW_TASK,
                        PERM_PERMIT_SURRENDER_PEER_REVIEW_EXECUTE_TASK));
        
        // SUBMIT_PERMIT_REVOCATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_REVOCATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_REVOCATION, VIEW_ONLY),
                List.of(PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(SUBMIT_PERMIT_REVOCATION, EXECUTE),
                List.of(PERM_PERMIT_REVOCATION_SUBMIT_VIEW_TASK,
                    PERM_PERMIT_REVOCATION_SUBMIT_EXECUTE_TASK));
        
        // PEER_REVIEW_PERMIT_REVOCATION
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_REVOCATION, NONE), List.of());
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_REVOCATION, VIEW_ONLY),
                List.of(PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
            .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_REVOCATION, EXECUTE),
                List.of(PERM_PERMIT_REVOCATION_PEER_REVIEW_VIEW_TASK,
                    PERM_PERMIT_REVOCATION_PEER_REVIEW_EXECUTE_TASK));
        
        // REVIEW_PERMIT_NOTIFICATION
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_NOTIFICATION, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_NOTIFICATION, VIEW_ONLY),
                        List.of(PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_PERMIT_NOTIFICATION, EXECUTE),
                        List.of(PERM_PERMIT_NOTIFICATION_REVIEW_VIEW_TASK,
                                PERM_PERMIT_NOTIFICATION_REVIEW_EXECUTE_TASK));

        // PEER_REVIEW_PERMIT_NOTIFICATION
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_NOTIFICATION, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_NOTIFICATION, VIEW_ONLY),
                        List.of(PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_NOTIFICATION, EXECUTE),
                        List.of(PERM_PERMIT_NOTIFICATION_PEER_REVIEW_VIEW_TASK,
                                PERM_PERMIT_NOTIFICATION_PEER_REVIEW_EXECUTE_TASK));
        
        // SUBMIT_REVIEW_PERMIT_VARIATION
        permissionGroupLevelsConfig
        		.put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_PERMIT_VARIATION, NONE), List.of());
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_PERMIT_VARIATION, VIEW_ONLY),
		                List.of(PERM_PERMIT_VARIATION_SUBMIT_REVIEW_VIEW_TASK));
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(SUBMIT_REVIEW_PERMIT_VARIATION, EXECUTE),
		                List.of(PERM_PERMIT_VARIATION_SUBMIT_REVIEW_VIEW_TASK,
		                		PERM_PERMIT_VARIATION_SUBMIT_REVIEW_EXECUTE_TASK));
		
	// PEER_REVIEW_PERMIT_VARIATION
        permissionGroupLevelsConfig
        		.put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_VARIATION, NONE), List.of());
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_VARIATION, VIEW_ONLY),
		                List.of(PERM_PERMIT_VARIATION_PEER_REVIEW_VIEW_TASK));
		permissionGroupLevelsConfig
		        .put(new RegulatorPermissionGroupLevel(PEER_REVIEW_PERMIT_VARIATION, EXECUTE),
		                List.of(PERM_PERMIT_VARIATION_PEER_REVIEW_VIEW_TASK,
		                		PERM_PERMIT_VARIATION_PEER_REVIEW_EXECUTE_TASK));

        // REVIEW_AER
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_AER, NONE), List.of());
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_AER, VIEW_ONLY),
                        List.of(PERM_AER_APPLICATION_REVIEW_VIEW_TASK));
        permissionGroupLevelsConfig
                .put(new RegulatorPermissionGroupLevel(REVIEW_AER, EXECUTE),
                        List.of(PERM_AER_APPLICATION_REVIEW_VIEW_TASK,
                                PERM_AER_APPLICATION_REVIEW_EXECUTE_TASK));

    }

    public List<Permission> getPermissionsFromPermissionGroupLevels(
        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissionGroupLevels) {
        List<Permission> permissions = new ArrayList<>();

        permissionGroupLevels.forEach((group, level) ->
            Optional.ofNullable(permissionGroupLevelsConfig.get(new RegulatorPermissionGroupLevel(group, level)))
                .ifPresent(permissions::addAll));

        return permissions;
    }

    public Map<RegulatorPermissionGroup, RegulatorPermissionLevel> getPermissionGroupLevelsFromPermissions(
        List<Permission> permissions) {

        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> permissionGroupLevels = new LinkedHashMap<>();
        permissionGroupLevelsConfig.forEach((configGroupLevel, configPermissionList) -> {
            if (permissions.containsAll(configPermissionList) &&
                isExistingLevelLessThanConfigLevel(permissionGroupLevels.get(configGroupLevel.getGroup()),
                    configGroupLevel)) {
                permissionGroupLevels.put(configGroupLevel.getGroup(), configGroupLevel.getLevel());
            }
        });

        return permissionGroupLevels;
    }

    public Map<RegulatorPermissionGroup, List<RegulatorPermissionLevel>> getPermissionGroupLevels() {
        return
            permissionGroupLevelsConfig.keySet().stream()
                .collect(Collectors.groupingBy(
                    RegulatorPermissionGroupLevel::getGroup,
                    LinkedHashMap::new,
                    Collectors.mapping(RegulatorPermissionGroupLevel::getLevel, toList())));
    }

    private boolean isExistingLevelLessThanConfigLevel(
        RegulatorPermissionLevel existingLevel, RegulatorPermissionGroupLevel configGroupLevel) {
        if (existingLevel == null) {
            return true;
        }
        return existingLevel.isLessThan(configGroupLevel.getLevel());
    }

}
