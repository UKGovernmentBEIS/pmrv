package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.OperatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemAssignedToOthersOperatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemAssignedToOthersOperatorService implements ItemAssignedToOthersService {

    private final ItemAssignedToOthersOperatorRepository itemAssignedToOthersOperatorRepository;
    private final ItemResponseService itemResponseService;
    private final OperatorAuthorityResourceAdapter operatorAuthorityResourceAdapter;

    /** {@inheritDoc} */
    @Override
    public ItemDTOResponse getItemsAssignedToOthers(PmrvUser pmrvUser, Long page, Long pageSize) {
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = operatorAuthorityResourceAdapter
            .findUserScopedRequestTaskTypesByAccounts(pmrvUser.getUserId(), pmrvUser.getAccounts());

        if (ObjectUtils.isEmpty(userScopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemAssignedToOthersOperatorRepository.findItemsAssignedToOther(
            pmrvUser.getUserId(),
            userScopedRequestTaskTypes,
            page,
            pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    /** {@inheritDoc} */
    @Override
    public ItemDTOResponse getItemsAssignedToOthersByAccount(PmrvUser pmrvUser, Long accountId,
                                                             Long page, Long pageSize) {
        Map<Long, Set<RequestTaskType>> userScopedRequestTaskTypes = operatorAuthorityResourceAdapter
            .findUserScopedRequestTaskTypesByAccounts(pmrvUser.getUserId(), Set.of(accountId));

        if (ObjectUtils.isEmpty(userScopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemAssignedToOthersOperatorRepository.findItemsAssignedToOther(
            pmrvUser.getUserId(),
            userScopedRequestTaskTypes,
            page,
            pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    /** {@inheritDoc} */
    @Override
    public RoleType getRoleType() {
        return RoleType.OPERATOR;
    }
}
