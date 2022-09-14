package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemRegulatorService implements ItemService {

    private final RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;
    private final ItemResponseService itemResponseService;
    private final ItemRegulatorRepository itemRegulatorRepository;

    @Override
    public ItemDTOResponse getItemsByRequest(PmrvUser pmrvUser, String requestId) {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = regulatorAuthorityResourceAdapter
                .findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemRegulatorRepository.findItemsByRequestId(scopedRequestTaskTypes, requestId);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    @Override
    public RoleType getRoleType() {
        return RoleType.REGULATOR;
    }
}
