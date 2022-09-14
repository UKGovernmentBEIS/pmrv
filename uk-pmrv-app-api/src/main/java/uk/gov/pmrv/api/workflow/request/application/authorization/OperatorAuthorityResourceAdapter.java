package uk.gov.pmrv.api.workflow.request.application.authorization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.services.resource.OperatorAuthorityResourceService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OperatorAuthorityResourceAdapter {
    private final OperatorAuthorityResourceService operatorAuthorityResourceService;

    public Map<Long, Set<RequestTaskType>> findUserScopedRequestTaskTypesByAccounts(String userId, Set<Long> accounts){
        Map<Long, Set<String>> requestTaskTypes = operatorAuthorityResourceService
                .findUserScopedRequestTaskTypesByAccounts(userId, accounts);
        return requestTaskTypes.entrySet().stream()
                .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream().map(RequestTaskType::valueOf).collect(Collectors.toSet())
                        )
                );
    }
}
