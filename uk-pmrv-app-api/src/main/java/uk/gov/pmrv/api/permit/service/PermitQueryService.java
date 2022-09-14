package uk.gov.pmrv.api.permit.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.PermitAuthorityInfoProvider;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.repository.PermitRepository;

@Service
@RequiredArgsConstructor
public class PermitQueryService implements PermitAuthorityInfoProvider {

    private final PermitRepository permitRepo;

    public PermitContainer getPermitContainerById(String id) {
        return permitRepo.findById(id)
            .map(PermitEntity::getPermitContainer)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public PermitContainer getPermitContainerByAccountId(Long accountId) {
        return permitRepo.findByAccountId(accountId)
            .map(PermitEntity::getPermitContainer)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Override
    public Long getPermitAccountById(String id) {
        return permitRepo.findPermitAccountById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    public Optional<String> getPermitIdByAccountId(Long accountId) {
        return permitRepo.findPermitIdByAccountId(accountId);
    }

    public Map<Long, PermitEntityAccountDTO> getPermitByAccountIds(List<Long> accountIds) {
        return permitRepo.findByAccountIdIn(accountIds).stream().collect(Collectors.toMap(PermitEntityAccountDTO::getAccountId, Function.identity()));
    }
}
