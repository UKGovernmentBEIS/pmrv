package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.VerifierAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemUnassignedVerifierRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemUnassignedVerifierService implements ItemUnassignedService {

    private final ItemUnassignedVerifierRepository itemUnassignedVerifierRepository;
    private final ItemResponseService itemResponseService;
    private final VerifierAuthorityResourceAdapter verifierAuthorityResourceAdapter;

    @Override
    public ItemDTOResponse getUnassignedItems(PmrvUser pmrvUser, Long page, Long pageSize) {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemUnassignedVerifierRepository.findUnassignedItems(
                pmrvUser.getUserId(),
                scopedRequestTaskTypes,
                page,
                pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    @Override
    public ItemDTOResponse getUnassignedItemsByAccount(PmrvUser pmrvUser, Long accountId, Long page, Long pageSize) {
        Map<Long, Set<RequestTaskType>> scopedRequestTaskTypes =
                verifierAuthorityResourceAdapter.findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemUnassignedVerifierRepository.findUnassignedItemsByAccount(
                pmrvUser.getUserId(),
                scopedRequestTaskTypes,
                accountId,
                page,
                pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.VERIFIER;
    }
}
