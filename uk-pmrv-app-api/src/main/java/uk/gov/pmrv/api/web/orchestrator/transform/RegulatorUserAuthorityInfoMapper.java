package uk.gov.pmrv.api.web.orchestrator.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.web.orchestrator.dto.RegulatorUserAuthorityInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RegulatorUserAuthorityInfoMapper {

    @Mapping(target = "locked", expression = "java(userInfo.getEnabled()!=null ? !userInfo.getEnabled() : null)")
    RegulatorUserAuthorityInfoDTO toUserAuthorityInfo(UserAuthorityDTO userAuthority, RegulatorUserInfoDTO userInfo);
}