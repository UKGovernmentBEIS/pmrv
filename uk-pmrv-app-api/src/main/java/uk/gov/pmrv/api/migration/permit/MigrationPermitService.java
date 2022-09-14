package uk.gov.pmrv.api.migration.permit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.InstallationCategoryMapper;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.permit.validation.PermitContextValidator;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationPermitService {


    private final PermitService permitService;
    private final AccountRepository accountRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final List<PermitContextValidator> permitContextValidators;

    @Transactional(propagation = Propagation.REQUIRED)
    public void migratePermit(Long accountId, PermitMigrationContainer permitMigrationContainer, List<String> migrationResults) {
        PermitContainer permit = permitMigrationContainer.getPermitContainer();
        List<FileAttachment> permitAttachments = permitMigrationContainer.getFileAttachments();

        try {
            fileAttachmentRepository.saveAll(permitAttachments);
            permitService.submitPermit(permit, accountId);

            updateAccountUponPermitMigration(permit, accountId);
        } catch (BusinessException e) {
            Optional<Account> account = accountRepository.findById(accountId);
            Arrays
                    .asList(e.getData())
                    .forEach(violation ->
                            migrationResults.add(constructErrorMessage(
                                    accountId,
                                    account.map(Account::getMigratedAccountId).orElse(""),
                                    account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                                    ((PermitViolation) violation).getSectionName(),
                                    ((PermitViolation) violation).getMessage(),
                                    getData((PermitViolation) violation))
                            ));
            throw e;
        } catch (ConstraintViolationException e) {
            //run validators anyway to collect all errors. validators should be made null safe
            Optional<Account> account = accountRepository.findById(accountId);
            e.getConstraintViolations().forEach((error -> migrationResults.add(constructErrorMessage(
                    accountId,
                    account.map(Account::getMigratedAccountId).orElse(""),
                    account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                    "",
                    error.getMessage(),
                    error.getPropertyPath() + ":" + error.getInvalidValue()))));

            List<PermitValidationResult> validationResults = new ArrayList<>();
            permitContextValidators.forEach(v -> validationResults.add(v.validate(permit)));

            boolean isValid = validationResults.stream().allMatch(PermitValidationResult::isValid);

            if (!isValid) {
                validationResults.forEach((error -> error.getPermitViolations().forEach(violation ->
                        migrationResults.add(constructErrorMessage(
                                accountId,
                                account.map(Account::getMigratedAccountId).orElse(""),
                                account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                                violation.getSectionName(),
                                violation.getMessage(),
                                getData(violation))))));
            }
            throw e;
        } catch (Exception e) {
            Optional<Account> account = accountRepository.findById(accountId);
            migrationResults.add(constructErrorMessage(
                    accountId,
                    account.map(Account::getMigratedAccountId).orElse(""),
                    account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null),
                    "",
                    e.getMessage(),
                    ""));
            throw e;
        }
        Optional<Account> account = accountRepository.findById(accountId);
        migrationResults.add(constructSuccessMessage(accountId,
                account.map(Account::getMigratedAccountId).orElse(""),
                account.map(account1 -> account1.getCompetentAuthority().getCode()).orElse(null)));
    }

    private void updateAccountUponPermitMigration(PermitContainer permitContainer, Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        EmitterType emitterType = permitContainer.getPermitType() == PermitType.HSE ? EmitterType.HSE : EmitterType.GHGE;
        account.setEmitterType(emitterType);
        account.setInstallationCategory(
            InstallationCategoryMapper.getInstallationCategory(emitterType, permitContainer.getPermit().getEstimatedAnnualEmissions().getQuantity()));

        accountRepository.save(account);
    }

    private String getData(PermitViolation violation) {
        StringBuilder builder = new StringBuilder();
        List<Object> collect = Arrays.stream(violation.getData())
                .map(data -> data instanceof Map.Entry ? ((Map.Entry<?, ?>) data).getValue() : data)
                .collect(Collectors.toList());
        collect.forEach(element -> builder.append("[").append(element).append("]"));
        return builder.toString();
    }

    private String constructErrorMessage(Long pmrvAccountId, String emitterId, String ca, String sectionName, String errorMessage, String data) {
        return "pmrvAccountId: " + pmrvAccountId +
                " | emitterId: " + emitterId +
                " | CA: " + ca +
                " | sectionName: " + sectionName +
                " | Error: " + errorMessage +
                " | data: " + data;
    }

    private String constructSuccessMessage(Long pmrvAccountId, String emitterId, String ca) {
        return "pmrvAccountId: " + pmrvAccountId +
                " | emitterId: " + emitterId +
                " | CA: " + ca;
    }
}
