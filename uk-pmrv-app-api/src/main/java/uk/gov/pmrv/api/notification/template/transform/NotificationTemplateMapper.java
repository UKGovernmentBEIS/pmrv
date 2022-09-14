package uk.gov.pmrv.api.notification.template.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;

@Mapper(componentModel = "spring", uses = TemplateInfoMapper.class, config = MapperConfig.class)
public interface NotificationTemplateMapper {

    @Mapping(target = "name", source = "name.name")
    NotificationTemplateDTO toNotificationTemplateDTO(NotificationTemplate notificationTemplate);
}
