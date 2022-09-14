package uk.gov.pmrv.api.migration.installationaccount;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.service.AccountVerificationBodyAppointService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
class InstallationAccountVbAppointService {

    private final AccountVerificationBodyAppointService accountVerificationBodyAppointService;
    private final VerificationBodyRepository verificationBodyRepository;

    @Transactional
    public void appointVerificationBodyToAccount(Long accountId, String etswapVbName) {
        Optional<VerificationBody> verificationBody = verificationBodyRepository.findByName(etswapVbName);

        verificationBody.ifPresentOrElse(
            vb -> {
                if (vb.getStatus() == VerificationBodyStatus.ACTIVE) {
                    accountVerificationBodyAppointService.appointVerificationBodyToAccount(vb.getId(), accountId);
                } else {
                    log.error("'{}' - Status of verification body '{}' is not active", accountId, vb.getName());
                }
            },
            () -> log.error("'{}' - Verification Body can not be resolved for ETSWAP value '{}'", accountId, etswapVbName)
        );
    }

}
