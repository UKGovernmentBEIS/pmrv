package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegulatorAuthorityResourceAdapter {
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    public Map<CompetentAuthority, Set<RequestTaskType>> findUserScopedRequestTaskTypes(String userId){
        Map<CompetentAuthority, Set<String>> requestTaskTypes = regulatorAuthorityResourceService
                .findUserScopedRequestTaskTypes(userId);
        return requestTaskTypes.entrySet().stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream().map(RequestTaskType::valueOf).collect(Collectors.toSet())
                        )
                );
    }
}
