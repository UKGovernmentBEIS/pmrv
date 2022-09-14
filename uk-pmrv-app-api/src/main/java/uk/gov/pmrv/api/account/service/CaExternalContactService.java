package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactsDTO;
import uk.gov.pmrv.api.account.repository.CaExternalContactRepository;
import uk.gov.pmrv.api.account.transform.CaExternalContactMapper;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class CaExternalContactService {

    private final CaExternalContactRepository caExternalContactRepository;

    private final CaExternalContactValidator caExternalContactValidator;

    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    private final CaExternalContactMapper caExternalContactMapper = Mappers.getMapper(CaExternalContactMapper.class);

    public CaExternalContactsDTO getCaExternalContacts(PmrvUser authUser) {
        CompetentAuthority ca = authUser.getCompetentAuthority();

        List<CaExternalContact> caExternalContacts = caExternalContactRepository.findByCompetentAuthority(ca);

        return CaExternalContactsDTO.builder()
            .caExternalContacts(caExternalContactMapper.toCaExternalContactDTOs(caExternalContacts))
            .isEditable(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER))
            .build();
    }
    
    public CaExternalContactDTO getCaExternalContactById(PmrvUser authUser, Long id) {
        CompetentAuthority ca = authUser.getCompetentAuthority();

        CaExternalContact caExternalContact = caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA));

        return caExternalContactMapper.toCaExternalContactDTO(caExternalContact);
    }
    
    public List<String> getCaExternalContactEmailsByIds(Set<Long> ids){
    	List<CaExternalContact> contacts = caExternalContactRepository.findAllByIdIn(ids);
    	
    	List<Long> idsMissing = ids.stream()
    		    .filter(id -> !contacts.stream().map(CaExternalContact::getId).collect(Collectors.toList()).contains(id))
    		    .collect(Collectors.toList());
    	
    	if(!idsMissing.isEmpty()) {
    		log.error("External contact ids are missing: " + idsMissing);
    		throw new BusinessException(ErrorCode.EXTERNAL_CONTACT_CA_MISSING, idsMissing);
    	} 
    	
		return contacts.stream().map(CaExternalContact::getEmail).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCaExternalContactById(PmrvUser authUser, Long id) {
        CompetentAuthority ca = authUser.getCompetentAuthority();

        Optional<CaExternalContact> byIdAndCompetentAuthority = caExternalContactRepository.findByIdAndCompetentAuthority(id, ca);
        if (byIdAndCompetentAuthority.isEmpty()) {
            throw  new BusinessException(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA);
        }

        caExternalContactRepository.deleteById(id);
    }

    @Transactional
    public void createCaExternalContact(PmrvUser authUser, CaExternalContactRegistrationDTO caExternalContactRegistration) {
        CompetentAuthority ca = authUser.getCompetentAuthority();
        caExternalContactValidator.validateCaExternalContactRegistration(ca, caExternalContactRegistration);

        CaExternalContact caExternalContact = caExternalContactMapper.toCaExternalContact(
            caExternalContactRegistration,
            ca.name());

        caExternalContactRepository.save(caExternalContact);
    }

    @Transactional
    public void editCaExternalContact(PmrvUser authUser, Long id, CaExternalContactRegistrationDTO caExternalContactRegistration) {
        CompetentAuthority ca = authUser.getCompetentAuthority();

        CaExternalContact caExternalContact = caExternalContactRepository.findByIdAndCompetentAuthority(id, ca)
            .orElseThrow(() -> new BusinessException(ErrorCode.EXTERNAL_CONTACT_NOT_RELATED_TO_CA));

        caExternalContactValidator.validateCaExternalContactRegistration(ca, id, caExternalContactRegistration);

        caExternalContact.setName(caExternalContactRegistration.getName());
        caExternalContact.setEmail(caExternalContactRegistration.getEmail());
        caExternalContact.setDescription(caExternalContactRegistration.getDescription());

        caExternalContactRepository.save(caExternalContact);
    }

}
