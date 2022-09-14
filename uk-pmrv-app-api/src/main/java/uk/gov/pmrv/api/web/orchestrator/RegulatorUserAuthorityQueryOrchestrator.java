package uk.gov.pmrv.api.web.orchestrator;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityQueryService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInfoService;
import uk.gov.pmrv.api.web.orchestrator.dto.RegulatorUserAuthorityInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.RegulatorUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.transform.RegulatorUserAuthorityInfoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegulatorUserAuthorityQueryOrchestrator {

    private final RegulatorAuthorityQueryService regulatorAuthorityQueryService;
    private final RegulatorUserInfoService regulatorUserInfoService;
    private final RegulatorUserAuthorityInfoMapper regulatorUserAuthorityInfoMapper = Mappers.getMapper(RegulatorUserAuthorityInfoMapper.class);

    public RegulatorUsersAuthoritiesInfoDTO getCaUsersAuthoritiesInfo(PmrvUser pmrvUser) {
        UserAuthoritiesDTO caAuthorities = regulatorAuthorityQueryService.getCaAuthorities(pmrvUser);
        List<String> userIds = caAuthorities.getAuthorities().stream().map(UserAuthorityDTO::getUserId).collect(Collectors.toList());

        List<RegulatorUserInfoDTO> regulatorAuthorityUsersInfo =
                regulatorUserInfoService.getRegulatorUsersInfo(pmrvUser, userIds);

        return getRegulatorUsersAuthoritiesInfo(caAuthorities, regulatorAuthorityUsersInfo);
    }

    private RegulatorUsersAuthoritiesInfoDTO getRegulatorUsersAuthoritiesInfo(UserAuthoritiesDTO regulatorUserAuthorities,
                                                                              List<RegulatorUserInfoDTO> regulatorUsersInfo) {

        List<RegulatorUserAuthorityInfoDTO> caUsers =
                regulatorUserAuthorities.getAuthorities().stream()
                        .map(authority ->
                                regulatorUserAuthorityInfoMapper.toUserAuthorityInfo(
                                        authority,
                                        regulatorUsersInfo.stream()
                                                .filter(info -> info.getId().equals(authority.getUserId()))
                                                .findFirst()
                                                .orElse(new RegulatorUserInfoDTO())))
                        .collect(Collectors.toList());

        return RegulatorUsersAuthoritiesInfoDTO.builder()
                .caUsers(caUsers)
                .editable(regulatorUserAuthorities.isEditable())
                .build();
    }
}
