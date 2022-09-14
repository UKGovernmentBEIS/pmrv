package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestActionAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestActionAuthorityInfoProvider;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;

@Service
@RequiredArgsConstructor
public class RequestActionAuthorityInfoQueryService implements RequestActionAuthorityInfoProvider {

    private final RequestActionRepository requestActionRepository;

    @Override
    public RequestActionAuthorityInfoDTO getRequestActionAuthorityInfo(Long requestActionId) {
        return requestActionRepository.findById(requestActionId)
                .map(requestAction -> RequestActionAuthorityInfoDTO.builder()
                        .id(requestAction.getId())
                        .type(requestAction.getType().name())
                        .authorityInfo(ResourceAuthorityInfo.builder()
                                .accountId(requestAction.getRequest().getAccountId())
                                .competentAuthority(requestAction.getRequest().getCompetentAuthority())
                                .verificationBodyId(requestAction.getRequest().getVerificationBodyId()).build())
                        .build())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
