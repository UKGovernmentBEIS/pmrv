package uk.gov.pmrv.api.workflow.request.application.item.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface ItemMapper {

    @Mapping(source = "taskAssignee", target = "itemAssignee.taskAssignee")
    @Mapping(source = "taskAssigneeType", target = "itemAssignee.taskAssigneeType")
    @Mapping(target = "daysRemaining", expression = "java(uk.gov.pmrv.api.workflow.utils.DateUtils.getDaysRemaining(item.getPauseDate(), item.getTaskDueDate()))")
    ItemDTO itemToItemDTO(Item item, 
                          UserInfoDTO taskAssignee, 
                          RoleType taskAssigneeType,
                          ItemAccountDTO account,
                          String permitId);

    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "name", target = "accountName")
    @Mapping(source = "legalEntity.name", target = "leName")
    ItemAccountDTO accountDTOToItemAccountDTO(AccountDTO accountDTO);
}
