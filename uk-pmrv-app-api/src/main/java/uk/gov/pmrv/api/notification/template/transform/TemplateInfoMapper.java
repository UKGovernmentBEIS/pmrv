package uk.gov.pmrv.api.notification.template.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.notification.template.domain.DocumentTemplate;
import uk.gov.pmrv.api.notification.template.domain.NotificationTemplate;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TemplateInfoMapper {

    @Mapping(target = "operatorType", ignore = true)
    @Mapping(target = "workflow", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    TemplateInfoDTO documentTemplateToTemplateInfoDTO(DocumentTemplate documentTemplate);

    @Mapping(target = "operatorType", ignore = true)
    @Mapping(target = "workflow", ignore = true)
    @Mapping(target = "lastUpdatedDate", ignore = true)
    @Mapping(target = "name", source = "name.name")
    TemplateInfoDTO notificationTemplateToTemplateInfoDTO(NotificationTemplate notificationTemplate);
}
