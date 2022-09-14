package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemOperatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemOperatorService implements ItemService {

    private final OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;
    private final ItemResponseService itemResponseService;
    private final ItemOperatorRepository itemOperatorRepository;

    @Override
    public ItemDTOResponse getItemsByRequest(PmrvUser pmrvUser, String requestId) {
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = operatorAuthorityResourceAdapter
                .findUserScopedRequestTaskTypesByAccounts(pmrvUser.getUserId(), pmrvUser.getAccounts());

        if (ObjectUtils.isEmpty(userScopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemOperatorRepository.findItemsByRequestId(userScopedRequestTaskTypes, requestId);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }
}
