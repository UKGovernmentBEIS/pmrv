package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.transform;

import org.mapstruct.Mapper;

import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;

/**
 * The AssigneeUserInfo Mapper.
 */
@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AssigneeUserInfoMapper {

    AssigneeUserInfoDTO toAssigneeUserInfoDTO(UserInfo userInfo);
}
