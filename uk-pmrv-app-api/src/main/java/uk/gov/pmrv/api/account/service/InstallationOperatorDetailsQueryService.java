package uk.gov.pmrv.api.account.service;

import org.mapstruct.factory.Mappers;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.transform.AccountToInstallationOperatorDetailsMapper;

@Service
@RequiredArgsConstructor
public class InstallationOperatorDetailsQueryService {

	private final AccountQueryService accountQueryService;
	private static final AccountToInstallationOperatorDetailsMapper accountToInstallationOperatorDetailsMapper = Mappers
			.getMapper(AccountToInstallationOperatorDetailsMapper.class);
    
    public InstallationOperatorDetails getInstallationOperatorDetails(Long accountId) {
		return accountToInstallationOperatorDetailsMapper
				.toPermitInstallationOperatorDetails(accountQueryService.getAccountDTOById(accountId));
    }
}
