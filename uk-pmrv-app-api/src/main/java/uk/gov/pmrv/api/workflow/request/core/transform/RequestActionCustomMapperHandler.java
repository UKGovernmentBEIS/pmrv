package uk.gov.pmrv.api.workflow.request.core.transform;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

@Service
@RequiredArgsConstructor
public class RequestActionCustomMapperHandler {

    private final List<RequestActionCustomMapper> mappers;

    public Optional<RequestActionCustomMapper> getMapper(final RequestActionType actionType, final RoleType roleType) {
        
        return mappers.stream().filter(m -> m.getRequestActionType().equals(actionType) &&
                                            m.getUserRoleType().equals(roleType))
                      .findFirst();
    }
}
