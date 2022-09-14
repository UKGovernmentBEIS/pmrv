package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestAuthorityInfoProvider;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@RequiredArgsConstructor
@Service
public class RequestAuthorityInfoQueryService implements RequestAuthorityInfoProvider {

    private final RequestService requestService;

    /**
     * Returns request by request id.
     *
     * @param id Request id
     * @return {@link RequestAuthorityInfoDTO}
     */
    @Override
    public RequestAuthorityInfoDTO getRequestInfo(String id) {
        Request request = requestService.findRequestById(id);
        return RequestAuthorityInfoDTO.builder()
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(request.getAccountId())
                        .competentAuthority(request.getCompetentAuthority())
                        .verificationBodyId(request.getVerificationBodyId()).build())
                .build();
    }
}
