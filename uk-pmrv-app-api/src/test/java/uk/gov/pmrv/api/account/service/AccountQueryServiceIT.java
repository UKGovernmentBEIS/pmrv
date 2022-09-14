package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.log4j.Log4j2;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Testcontainers
@SpringBootTest(
    properties = {
        "camunda.bpm.enabled=false"
    }
)
@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AccountQueryServiceIT extends AbstractContainerBaseTest {

    public static final long TEST_ACCOUNT_ID = 1L;

    @Autowired
    private AccountQueryService cut;
    @Autowired
    private AccountRepository repository;
    @Autowired
    private TransactionTemplate txTemplate;

    @MockBean
    AccountMapper accountMapper;

    @MockBean
    RuntimeService runtimeService;
    @MockBean
    TaskService taskService;

    @BeforeEach
    @BeforeTransaction // without this annotation, the test does not see the db entry
    public void setUp() {
        repository.save(
            Account.builder()
                .id(TEST_ACCOUNT_ID)
                .accountType(AccountType.INSTALLATION)
                .commencementDate(LocalDate.now())
                .competentAuthority(CompetentAuthority.ENGLAND)
                .verificationBodyId(TEST_ACCOUNT_ID)
                .status(AccountStatus.NEW)
                .name("account1")
                .siteName("account1")
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM00001")
                .build()
        );
    }

    @AfterEach
    @AfterTransaction // without this annotation, the entry is not actually deleted
    public void tearDown() {
        repository.deleteById(TEST_ACCOUNT_ID);
    }

    @Test
    @DisplayName("should use write lock and read correct account status from second thread when status updated in first thread")
    void shouldUseWriteLock() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // both threads wait for this latch. this way they start at the same time
        CountDownLatch startLatch = new CountDownLatch(1);
        // first thread locks the db row then releases this latch, to ensure that second thread always asks for the lock second
        CountDownLatch startSecondThreadLatch = new CountDownLatch(1);
        // main thread waits for this latch, so it can assert after both threads have run.
        CountDownLatch stopLatch = new CountDownLatch(2);
        // we can assert only form main thread, so we need to access the result of the query of 2nd thread in the main thread
        AtomicReference<Account> accountFromSecondThread = new AtomicReference<>();

        Runnable r1 = () -> {
            try {
                txTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        await(startLatch);
                        Account account = cut.getAccountByIdForUpdate(TEST_ACCOUNT_ID);
                        startSecondThreadLatch.countDown();
                        account.setStatus(AccountStatus.AWAITING_SURRENDER);
                        repository.save(account);
                    }
                });
            } finally {
                stopLatch.countDown();
            }
        };

        Runnable r2 = () -> {
            try {
                Account account = txTemplate.execute(new TransactionCallback<>() {
                    @Override
                    public Account doInTransaction(TransactionStatus status) {
                        await(startSecondThreadLatch);
                        return cut.getAccountByIdForUpdate(TEST_ACCOUNT_ID);
                    }
                });
                accountFromSecondThread.set(account);
            } finally {
                stopLatch.countDown();
            }
        };

        executorService.execute(r1);
        executorService.execute(r2);
        // first thread executes
        startLatch.countDown();
        // both threads have finished
        await(stopLatch);
        // clean up
        executorService.shutdown();
        // NOTE: to see this tess fail, comment out the @Lock annotation in AccountRepository's method
        assertThat(accountFromSecondThread.get().getStatus()).isEqualTo(AccountStatus.AWAITING_SURRENDER);
    }

    private void await(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
