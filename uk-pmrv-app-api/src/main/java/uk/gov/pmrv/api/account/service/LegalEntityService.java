package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.account.transform.LegalEntityMapper;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LegalEntityService {

    private final LegalEntityRepository legalEntityRepository;
    private final LegalEntityMapper legalEntityMapper;

    /**
     * Returns the related legal entities for the authenticated user.
     *
     * @param pmrvUser {@link PmrvUser}
     * @return List of {@link LegalEntityInfoDTO}
     */
    public List<LegalEntityInfoDTO> getUserLegalEntities(PmrvUser pmrvUser) {
        final List<LegalEntity> legalEntities;
        switch (pmrvUser.getRoleType()) {
            case REGULATOR:
                legalEntities = legalEntityRepository.findActive();
                break;
            case OPERATOR:
                legalEntities = legalEntityRepository.findActiveLegalEntitiesByAccounts(pmrvUser.getAccounts());
                break;
            default:
                legalEntities = Collections.emptyList();
        }

        return legalEntityMapper.toLegalEntityInfoDTOs(legalEntities);
    }

    /**
     * Checks if an active legal entity exists for the provided user.
     *
     * @param leName Legal entity name
     * @param pmrvUser {@link PmrvUser}
     * @return True if an active legal entity exists
     */
    public boolean isExistingActiveLegalEntityName(String leName, PmrvUser pmrvUser) {
        if(pmrvUser == null) {
            return legalEntityRepository.existsActiveLegalEntity(leName);
        }

        switch (pmrvUser.getRoleType()) {
            case REGULATOR:
                return legalEntityRepository.existsActiveLegalEntity(leName);
            case OPERATOR:
                return legalEntityRepository.existsActiveLegalEntityNameInAnyOfAccounts(leName, pmrvUser.getAccounts());
            default:
                throw new BusinessException(ErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER);
        }
    }

    public boolean isExistingActiveLegalEntityName(String leName) {
        return isExistingActiveLegalEntityName(leName, null);
    }

    /**
     * Returns legal entity with the provided id
     *
     * @param id Legal entity id
     * @return {@link LegalEntity}
     */
    public LegalEntity getLegalEntityById(Long id) {
        return legalEntityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    LegalEntity getUserLegalEntityById(Long legalEntityId, PmrvUser pmrvUser) {
        LegalEntity legalEntity = getLegalEntityById(legalEntityId);

        switch (pmrvUser.getRoleType()) {
            case REGULATOR:
                break;
            case OPERATOR:
                if(!legalEntityRepository.existsLegalEntityInAnyOfAccounts(legalEntity.getId(), pmrvUser.getAccounts())) {
                    throw new BusinessException(ErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER);
                }
                break;
            default:
                throw new BusinessException(ErrorCode.LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER);
        }

        return legalEntity;
    }

    /**
     * Returns the requested legal entity associated with the user account.
     *
     * @param legalEntityId Legal entity id
     * @param pmrvUser {@link PmrvUser}
     * @return {@link LegalEntityDTO}
     */
    public LegalEntityDTO getUserLegalEntityDTOById(Long legalEntityId, PmrvUser pmrvUser) {
        LegalEntity legalEntity = getUserLegalEntityById(legalEntityId, pmrvUser);
        return legalEntityMapper.toLegalEntityDTO(legalEntity);
    }

    public void deleteLegalEntity(LegalEntity legalEntity) {
        legalEntityRepository.delete(legalEntity);
    }

    public void handleLegalEntityDenied(LegalEntity legalEntity) {
        if(legalEntity.getStatus().equals(LegalEntityStatus.PENDING)){
            legalEntity.setStatus(LegalEntityStatus.DENIED);
            legalEntityRepository.save(legalEntity);
        }
    }

    public LegalEntity createLegalEntity(LegalEntityDTO legalEntityDTO, LegalEntityStatus status) {
        LegalEntity le = legalEntityMapper.toLegalEntity(legalEntityDTO);
        le.setStatus(status);
        return legalEntityRepository.save(le);
    }

    public LegalEntity resolveLegalEntity(LegalEntityDTO legalEntityDTO, PmrvUser authUser) {
        if(legalEntityDTO.getId() != null) {
            return getUserLegalEntityById(legalEntityDTO.getId(), authUser);
        } else {
            if (isExistingActiveLegalEntityName(legalEntityDTO.getName(), authUser)) {
                throw new BusinessException(ErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
            }
            return legalEntityRepository.findByNameAndStatus(legalEntityDTO.getName(), LegalEntityStatus.PENDING)
                    .orElseGet(() -> createLegalEntity(legalEntityDTO, LegalEntityStatus.PENDING));
        }
    }

    public LegalEntity resolveAmendedLegalEntity(LegalEntityDTO newLegalEntityDTO, LegalEntity currentLegalEntity, PmrvUser authUser) {
        if (newLegalEntityDTO.getId() != null ||
                !Objects.equals(newLegalEntityDTO.getName(), currentLegalEntity.getName())) {
            return resolveLegalEntity(newLegalEntityDTO, authUser);
        } else {
            return currentLegalEntity;
        }
    }

    public LegalEntity activateLegalEntity(LegalEntityDTO latestLegalEntityDTO) {
        if(latestLegalEntityDTO.getId() != null) {
            // already activated. just return it
            return getLegalEntityById(latestLegalEntityDTO.getId());
        }

        if (isExistingActiveLegalEntityName(latestLegalEntityDTO.getName())) {
            throw new BusinessException(ErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        }

        // If LE status is changed from another installation account create a new one
        LegalEntity legalEntity = legalEntityRepository
                .findByNameAndStatus(latestLegalEntityDTO.getName(), LegalEntityStatus.PENDING)
                .orElseGet(() -> createLegalEntity(latestLegalEntityDTO, LegalEntityStatus.PENDING));

        LegalEntity latestLegalEntity = legalEntityMapper.toLegalEntity(latestLegalEntityDTO);

        latestLegalEntity.setId(legalEntity.getId());
        latestLegalEntity.getLocation().setId(legalEntity.getLocation().getId());
        latestLegalEntity.setStatus(LegalEntityStatus.ACTIVE);
        return legalEntityRepository.save(latestLegalEntity);
    }

}
