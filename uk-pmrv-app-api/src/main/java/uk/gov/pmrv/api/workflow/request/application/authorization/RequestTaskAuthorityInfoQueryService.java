package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestTaskAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestTaskAuthorityInfoProvider;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

@Service
@RequiredArgsConstructor
public class RequestTaskAuthorityInfoQueryService implements RequestTaskAuthorityInfoProvider {

    private final RequestTaskService requestTaskService;

    @Override
    @Transactional(readOnly = true)
    public RequestTaskAuthorityInfoDTO getRequestTaskInfo(Long requestTaskId) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        return RequestTaskAuthorityInfoDTO.builder()
                .type(requestTask.getType().name())
                .requestType(requestTask.getRequest().getType().name())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(requestTask.getRequest().getAccountId())
                        .competentAuthority(requestTask.getRequest().getCompetentAuthority())
                        .verificationBodyId(requestTask.getRequest().getVerificationBodyId()).build())
                .assignee(requestTask.getAssignee())
                .build();
    }
}
