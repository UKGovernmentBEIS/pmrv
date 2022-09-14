package uk.gov.pmrv.api.account.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.Location;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountStatus;
import uk.gov.pmrv.api.account.transform.InstallationCategoryMapper;
import uk.gov.pmrv.api.account.transform.LocationMapper;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.transform.AddressMapper;

@Service
@RequiredArgsConstructor
public class AccountUpdateService {

    private final AccountQueryService accountQueryService;
    private final AccountRepository accountRepository;
    private final AccountStatusService accountStatusService;
    private final LegalEntityValidationService legalEntityValidationService;
    private static final LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);
    private static final AddressMapper addressMapper = Mappers.getMapper(AddressMapper.class);

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountSiteName(Long accountId, String newSiteName) {
        Account account = accountQueryService.getAccountById(accountId);
        account.setSiteName(newSiteName);
        accountRepository.save(account);
    }
    
    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountRegistryId(Long accountId, Integer registryId) {
        Account account = accountQueryService.getAccountById(accountId);
        account.setRegistryId(registryId);
        accountRepository.save(account);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountSopId(Long accountId, Long sopId) {
        Account account = accountQueryService.getAccountById(accountId);
        account.setSopId(sopId);
        accountRepository.save(account);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateAccountAddress(Long accountId, LocationDTO address) {
        Account account = accountQueryService.getAccountById(accountId);
        Location location = locationMapper.toLocation(address);
        account.setLocation(location);
        accountRepository.save(account);
    }

    @Transactional
    public void updateAccountUponPermitGranted(Long accountId, EmitterType emitterType, BigDecimal estimatedAnnualEmissions) {
        Account account = accountQueryService.getAccountById(accountId);
        account.setEmitterType(emitterType);
        account.setInstallationCategory(InstallationCategoryMapper.getInstallationCategory(emitterType, estimatedAnnualEmissions));

        accountStatusService.handlePermitGranted(accountId);
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateLegalEntityAddress(Long accountId, AddressDTO addressDTO) {
        Account account = accountQueryService.getAccountById(accountId);
        LegalEntity legalEntity = account.getLegalEntity();
        Address address = addressMapper.toAddress(addressDTO);
        legalEntity.setLocation(new LocationOnShore(null, address));
    }

    @Transactional
    @AccountStatus(expression = "{#status != 'UNAPPROVED' && #status != 'DENIED'}")
    public void updateLegalEntityName(final Long accountId, final String name) {
        final Account account = accountQueryService.getAccountById(accountId);
        final LegalEntity legalEntity = account.getLegalEntity();

        legalEntityValidationService.validateNameExistenceInOtherActiveLegalEntities(name, legalEntity.getId());

        legalEntity.setName(name);
    }
}
