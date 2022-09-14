package uk.gov.pmrv.api.verificationbody.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.domain.transform.AddressMapper;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyEditDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateStatusDTO;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusDisabledEvent;
import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyStatusEnabledEvent;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.event.AccreditationEmissionTradingSchemeNotAvailableEvent;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

@Service
@RequiredArgsConstructor
public class VerificationBodyUpdateService {

    private final VerificationBodyRepository verificationBodyRepository;
    private final AccreditationRefNumValidationService accreditationRefNumValidationService;
    private final ApplicationEventPublisher eventPublisher;
    private final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    @Transactional
    public void updateVerificationBody(VerificationBodyUpdateDTO verificationBodyUpdateDTO) {
        Long verificationBodyId = verificationBodyUpdateDTO.getId();
        VerificationBody verificationBody =
                verificationBodyRepository.findByIdEagerEmissionTradingSchemes(verificationBodyId)
                        .orElseThrow(() -> {
                            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
                        });
        VerificationBodyEditDTO vbUpdate = verificationBodyUpdateDTO.getVerificationBody();

        accreditationRefNumValidationService.validate(vbUpdate.getAccreditationReferenceNumber(), verificationBodyId);

        Set<EmissionTradingScheme> removedEmissionTradingSchemes = retrieveRemovedEmissionTradingSchemes(verificationBody, vbUpdate);

        //do update verification body 
        updateVerificationBodyProperties(verificationBody, vbUpdate);

        //publish event for not available accr ref number emission trading schemes
        if(!removedEmissionTradingSchemes.isEmpty()) {
            eventPublisher.publishEvent(
                    new AccreditationEmissionTradingSchemeNotAvailableEvent(verificationBodyId, removedEmissionTradingSchemes)
            );
        }
    }

    @Transactional
    public void updateVerificationBodiesStatus(List<VerificationBodyUpdateStatusDTO> verificationBodyUpdateStatusList) {
        Map<VerificationBodyStatus, Set<Long>> updateStatus = verificationBodyUpdateStatusList.stream()
                .collect(Collectors.groupingBy(VerificationBodyUpdateStatusDTO::getStatus,
                        Collectors.mapping(VerificationBodyUpdateStatusDTO::getId, Collectors.toSet())));

        if(updateStatus.containsKey(VerificationBodyStatus.ACTIVE)
                && !updateStatus.get(VerificationBodyStatus.ACTIVE).isEmpty()){
            updateStatusToActive(updateStatus.get(VerificationBodyStatus.ACTIVE));
        }

        if(updateStatus.containsKey(VerificationBodyStatus.DISABLED)
                && !updateStatus.get(VerificationBodyStatus.DISABLED).isEmpty()){
            updateStatusToDisabled(updateStatus.get(VerificationBodyStatus.DISABLED));
        }
    }

    private void updateStatusToActive(Set<Long> verificationBodyIds) {
        List<VerificationBody> verificationBodies = verificationBodyRepository.findAllByIdIn(verificationBodyIds);

        if (verificationBodies.size() != verificationBodyIds.size()) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_DOES_NOT_EXIST);
        }

        verificationBodies.forEach(verificationBody -> verificationBody.setStatus(VerificationBodyStatus.ACTIVE));
        verificationBodyRepository.saveAll(verificationBodies);

        // Publish event for changing status to ENABLED
        eventPublisher.publishEvent(new VerificationBodyStatusEnabledEvent(verificationBodyIds));
    }

    private void updateStatusToDisabled(Set<Long> verificationBodyIds) {
        List<VerificationBody> verificationBodies = verificationBodyRepository.findAllByIdIn(verificationBodyIds);

        if (verificationBodies.size() != verificationBodyIds.size()) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_DOES_NOT_EXIST);
        }

        verificationBodies.forEach(verificationBody -> verificationBody.setStatus(VerificationBodyStatus.DISABLED));
        verificationBodyRepository.saveAll(verificationBodies);

        // Publish event for changing status to DISABLED
        eventPublisher.publishEvent(new VerificationBodyStatusDisabledEvent(verificationBodyIds));
    }

    private void updateVerificationBodyProperties(VerificationBody vb, VerificationBodyEditDTO vbUpdate) {
        //update name
        vb.setName(vbUpdate.getName());

        //update accreditation reference number
        vb.setAccreditationReferenceNumber(vbUpdate.getAccreditationReferenceNumber());

        //update address fields
        vb.setAddress(addressMapper.toAddress(vbUpdate.getAddress()));

        //update emission trading schemes
        vb.setEmissionTradingSchemes(vbUpdate.getEmissionTradingSchemes());

        verificationBodyRepository.save(vb);
    }

    private Set<EmissionTradingScheme> retrieveRemovedEmissionTradingSchemes(VerificationBody vb,
                                                                             VerificationBodyEditDTO vbUpdate) {

        Set<EmissionTradingScheme> existingEmissionTradingSchemes = vb.getEmissionTradingSchemes();
        Set<EmissionTradingScheme> newEmissionTradingSchemes = vbUpdate.getEmissionTradingSchemes();

        Set<EmissionTradingScheme> diffs = new HashSet<>(existingEmissionTradingSchemes);
        diffs.removeAll(newEmissionTradingSchemes);
        return diffs;
    }
}
