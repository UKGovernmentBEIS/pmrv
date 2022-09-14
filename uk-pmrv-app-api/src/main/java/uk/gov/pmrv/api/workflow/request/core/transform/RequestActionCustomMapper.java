package uk.gov.pmrv.api.workflow.request.core.transform;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

public interface RequestActionCustomMapper {

    RequestActionDTO toRequestActionDTO(RequestAction requestAction);

    RequestActionType getRequestActionType();

    RoleType getUserRoleType();
}
