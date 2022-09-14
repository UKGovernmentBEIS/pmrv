package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoResponseDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;
import uk.gov.pmrv.api.verificationbody.transform.VerificationBodyMapper;

import java.util.List;

@RequiredArgsConstructor
@Service
public class VerificationBodyQueryService {

    private final VerificationBodyRepository verificationBodyRepository;
    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private static final VerificationBodyMapper verificationBodyMapper = Mappers.getMapper(VerificationBodyMapper.class);

    public VerificationBodyInfoResponseDTO getVerificationBodies(PmrvUser pmrvUser) {
        List<VerificationBodyInfoDTO> verificationBodies = verificationBodyMapper
                .toVerificationBodyInfoDTO(verificationBodyRepository.findAll());

        // Check if user has the permission of editing VBs
        CompetentAuthority ca = pmrvUser.getCompetentAuthority();
        boolean isEditable = compAuthAuthorizationResourceService.hasUserScopeToCompAuth(pmrvUser, ca, Scope.MANAGE_VB);

        return VerificationBodyInfoResponseDTO.builder().verificationBodies(verificationBodies).editable(isEditable).build();
    }
    
    @Transactional(readOnly = true)
    public VerificationBodyDTO getVerificationBodyById(Long verificationBodyId) {
        VerificationBody verificationBody = 
                verificationBodyRepository
                    .findByIdEagerEmissionTradingSchemes(verificationBodyId)
                    .orElseThrow(() -> {
                        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, verificationBodyId);
                    });
        return verificationBodyMapper.toVerificationBodyDTO(verificationBody);
    }
    
    public VerificationBodyNameInfoDTO getVerificationBodyNameInfoById(Long verificationBodyId){
        VerificationBody vb = 
                verificationBodyRepository
                    .findById(verificationBodyId)
                    .orElseThrow(() -> {
                        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
                    });
        return verificationBodyMapper.toVerificationBodyNameInfoDTO(vb);
    }
    
    public List<VerificationBodyNameInfoDTO> getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme(
            EmissionTradingScheme emissionTradingScheme) {
        return verificationBodyRepository.findActiveVerificationBodiesAccreditedToEmissionTradingScheme(emissionTradingScheme);
    }
    
    public boolean existsVerificationBodyById(Long verificationBodyId) {
        return verificationBodyRepository.existsById(verificationBodyId);
    }

    public boolean existsActiveVerificationBodyById(Long verificationBodyId) {
        return verificationBodyRepository.existsByIdAndStatus(verificationBodyId, VerificationBodyStatus.ACTIVE);
    }

    public boolean existsNonDisabledVerificationBodyById(Long verificationBodyId) {
        return verificationBodyRepository.existsByIdAndStatusNot(verificationBodyId, VerificationBodyStatus.DISABLED);
    }
    
    public boolean isVerificationBodyAccreditedToEmissionTradingScheme(Long verificationBodyId, EmissionTradingScheme emissionTradingScheme) {
        return verificationBodyRepository.isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, emissionTradingScheme);
    }
    
}
