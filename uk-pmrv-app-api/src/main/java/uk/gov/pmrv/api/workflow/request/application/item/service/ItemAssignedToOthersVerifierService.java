package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemAssignedToOthersVerifierRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemAssignedToOthersVerifierService implements ItemAssignedToOthersService {

    private final ItemAssignedToOthersVerifierRepository itemAssignedToOthersVerifierRepository;
    private final ItemResponseService itemResponseService;
    private final VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getItemsAssignedToOthers(PmrvUser pmrvUser, Long page, Long pageSize) {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemAssignedToOthersVerifierRepository.findItemsAssignedToOthers(
                pmrvUser.getUserId(),
                scopedRequestTaskTypes,
                page,
                pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    @Override
    public ItemDTOResponse getItemsAssignedToOthersByAccount(PmrvUser pmrvUser, Long accountId, Long page, Long pageSize) {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemAssignedToOthersVerifierRepository.findItemsAssignedToOthersByAccount(
                pmrvUser.getUserId(),
                accountId,
                scopedRequestTaskTypes,
                page,
                pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
