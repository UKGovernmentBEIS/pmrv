package uk.gov.pmrv.api.workflow.request.application.requestaction;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapper;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapperHandler;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionMapper;

@RequiredArgsConstructor
@Service
public class RequestActionQueryService {
    
    private final RequestActionRepository requestActionRepository;
    private final AuthorizationRulesQueryService authorizationRulesQueryService;
    private final RequestActionMapper requestActionMapper = Mappers.getMapper(RequestActionMapper.class);
    private final RequestActionCustomMapperHandler customMapperHandler;

    
    @Transactional(readOnly = true)
    public RequestActionDTO getRequestActionById(Long requestActionId, PmrvUser pmrvUser) {
        final RequestAction requestAction = requestActionRepository.findById(requestActionId).orElseThrow(() -> {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        });
        
        final RequestActionType requestActionType = requestAction.getType();
        final RoleType roleType = pmrvUser.getRoleType();
        final Optional<RequestActionCustomMapper> customMapper = customMapperHandler.getMapper(requestActionType, roleType);
        return customMapper.isPresent() ?
            customMapper.get().toRequestActionDTO(requestAction) :
            requestActionMapper.toRequestActionDTO(requestAction);
    }
    
    @Transactional(readOnly = true)
    public List<RequestActionInfoDTO> getRequestActionsByRequestId(String requestId, PmrvUser authUser) {
        List<RequestAction> requestActions = requestActionRepository.findAllByRequestId(requestId);
        List<RequestAction> userGrantedRequestActions = filterUserGrantedRequestActions(authUser, requestActions);
        return userGrantedRequestActions.stream()
                .map(requestActionMapper::toRequestActionInfoDTO)
                .collect(Collectors.toList());
    }
    
    private List<RequestAction> filterUserGrantedRequestActions(PmrvUser authUser, List<RequestAction> requestActions) {
        Set<String> userAllowedRequestActionTypes = authorizationRulesQueryService
                .findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_ACTION, authUser.getRoleType());
        
        return requestActions.stream()
                .filter(requestAction -> userAllowedRequestActionTypes.contains(requestAction.getType().name()))
                .collect(Collectors.toList());
    }

}
