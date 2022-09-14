package uk.gov.pmrv.api.migration.installationaccount.contacts;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountContactTypeUpdateValidator;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.utils.ExceptionUtils;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.installationaccount.InstallationAccountHelper;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationInstallationAccountContactsService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final UserAuthService userAuthService;
    private final AccountRepository accountRepository;
    private final AuthorityService authorityService;
    private final List<AccountContactTypeUpdateValidator> contactTypeValidators;

    private static final String QUERY_BASE =
        "select e.fldEmitterId AS account_id,\r\n" +
            "e.fldName AS name,\r\n" +
            "e.fldEmitterDisplayId AS emitter_display_id,\r\n" +
            "e.fldPrimaryOperationalContactID AS primary_contact_id,\r\n" +
            "prim.fldEmailAddress AS primary_contact_email,\r\n" +
            "e.fldSecondaryOperationalContactID AS secondary_contact_id,\r\n" +
            "sec.fldEmailAddress AS secondary_contact_email,\r\n" +
            "e.fldServiceContactID AS service_contact_id,\r\n" +
            "ser.fldEmailAddress AS service_contact_email,\r\n" +
            "e.fldFinanceContactID AS finance_contact_id,\r\n" +
            "fin.fldEmailAddress AS finance_contact_email\r\n" +
            "from tblEmitter e\r\n" +
            " inner join tlkpEmitterType et on e.fldEmitterTypeID = et.fldEmitterTypeID\r\n" +
            " inner join tlkpEmitterStatus es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n" +
            "  left join tblContact prim on e.fldPrimaryOperationalContactID = prim.fldContactID\r\n" +
            "  left join tblContact sec on e.fldSecondaryOperationalContactID = sec.fldContactID\r\n" +
            "  left join tblContact ser on e.fldServiceContactID = ser.fldContactID\r\n" +
            "  left join tblContact fin on e.fldFinanceContactID = fin.fldContactID\r\n" +
            " where et.fldName = 'Installation'\r\n" +
            "   and es.fldDisplayName = 'Live';";

    @Override
    public String getResource() {
        return "installation-accounts-contacts";
    }

    @Override
    public List<String> migrate(String ids) {
        final String query = InstallationAccountHelper.constructQuery(QUERY_BASE, ids);
        List<String> failed = new ArrayList<>();
        List<EmitterContacts> emitterContactsList = migrationJdbcTemplate.query(query, new EmitterContactsMapper());
        for (EmitterContacts emitterContacts: emitterContactsList) {
            try {
                failed.addAll(migrateInstallationAccountContacts(emitterContacts));
            } catch (Exception ex) {
                log.error("migration of installation account contacts: {} failed with {}",
                    emitterContacts.getEmitterId(), ExceptionUtils.getRootCause(ex).getMessage());
                failed.add("Id: " + emitterContacts.getEmitterId() + " | Error: " + ExceptionUtils.getRootCause(ex).getMessage());
            }
        }
        return failed;
    }

    private List<String> migrateInstallationAccountContacts(EmitterContacts emitterContacts) {
        List<String> failedEntries = new ArrayList<>();
        String accountId = emitterContacts.getEmitterId();

        Account migratedAccount = accountRepository.findByMigratedAccount(accountId);

        if (migratedAccount == null) {
            failedEntries.add(InstallationAccountHelper.constructErrorMessage(accountId, emitterContacts.getEmitterName(), emitterContacts.getEmitterDisplayId(),
                    "account with migrated id does not exist", accountId));
        } else {
            Map<AccountContactType, String> accountContacts = constructAccountContacts(emitterContacts);

            failedEntries.addAll(
                validateAccountContactTypesAndUsers(accountContacts, migratedAccount.getId(), migratedAccount.getMigratedAccountId(),
                        emitterContacts.getEmitterName(), emitterContacts.getEmitterDisplayId())
            );

            if (failedEntries.isEmpty()) {
                doAssignAccountContacts(accountContacts, migratedAccount);
            }
        }

        return failedEntries;
    }

    private Map<AccountContactType, String> constructAccountContacts(EmitterContacts emitterContacts) {
        Map<AccountContactType, String> accountContacts = new HashMap<>();

        fillAccountContactsMap(accountContacts, AccountContactType.PRIMARY, emitterContacts.getPrimaryContactEmail());
        fillAccountContactsMap(accountContacts, AccountContactType.SECONDARY, emitterContacts.getSecondaryContactEmail());
        fillAccountContactsMap(accountContacts, AccountContactType.SERVICE, emitterContacts.getServiceContactEmail());
        fillAccountContactsMap(accountContacts, AccountContactType.FINANCIAL, emitterContacts.getFinanceContactEmail());

        accountContacts.replaceAll((contactType, userEmail) -> getUserIdByEmail(userEmail));
        accountContacts.values().removeIf(Objects::isNull);
        return accountContacts;
    }

    private void fillAccountContactsMap(Map<AccountContactType, String> accountContacts,
                                        AccountContactType accountContactType, String email) {
        if (!ObjectUtils.isEmpty(email)) {
            accountContacts.put(accountContactType, email);
        }
    }

    private String getUserIdByEmail(String email) {
        UserInfoDTO userInfoDTO = userAuthService.getUserByEmail(email).orElse(null);
        return userInfoDTO != null ? userInfoDTO.getUserId() : null;
    }

    /**
     * During migration of account contacts, the following validations are performed :
     * 1.Each candidate user to be assigned as account contact is related to the account
     * 2.All {@link AccountContactTypeUpdateValidator} validations
     *
     * Main difference with {@link uk.gov.pmrv.api.account.service.AccountContactUpdateService#updateAccountContacts(Map, Long)}
     * is that during migration we do not check that candidate users are active for the {@code accountId}
     */
    private List<String> validateAccountContactTypesAndUsers(Map<AccountContactType, String> accountContacts,
                                                             Long accountId, String migratedAccountId, String name, String emitterDisplayId) {
        List<String> failedEntries = new ArrayList<>();

        //check if candidate account contact users are related to the account
        accountContacts.forEach((contactType, userId) -> {
            if (!authorityService.existsByUserIdAndAccountId(userId, accountId)) {
                failedEntries.add(InstallationAccountHelper.constructErrorMessage(migratedAccountId, name, emitterDisplayId,
                    "User is not related to account with id " + accountId, userId));
            }
        });

        //perform contact type validations
        contactTypeValidators.forEach(v -> {
            try {
                v.validateUpdate(accountContacts, accountId);
            } catch (BusinessException ex) {
                failedEntries.add(InstallationAccountHelper.constructErrorMessage(migratedAccountId, name, emitterDisplayId, ExceptionUtils.getRootCause(ex).getMessage(), ""));
            }
        });

        return failedEntries;
    }

    private void doAssignAccountContacts(Map<AccountContactType, String> accountContacts, Account migratedAccount) {
        accountContacts.forEach((accountContactType, userId) -> migratedAccount.getContacts().put(accountContactType, userId));
        accountRepository.save(migratedAccount);
    }
}
