package uk.gov.pmrv.api;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.Arrays;
import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static java.util.stream.Collectors.toList;
import static uk.gov.pmrv.api.ArchUnitTest.BASE_PACKAGE;

@AnalyzeClasses(packages = BASE_PACKAGE, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTest {

    static final String BASE_PACKAGE = "uk.gov.pmrv.api";

    static final String ACCOUNT_PACKAGE = BASE_PACKAGE + ".account..";
    static final String AUTHORIZATION_PACKAGE = BASE_PACKAGE + ".authorization..";
    static final String COMMON_PACKAGE = BASE_PACKAGE + ".common..";
    static final String FILES_PACKAGE = BASE_PACKAGE + ".files..";
    static final String MIGRATION_PACKAGE = BASE_PACKAGE + ".migration..";
    static final String NOTIFICATION_PACKAGE = BASE_PACKAGE + ".notification..";
    static final String PAYMENT_PACKAGE = BASE_PACKAGE + ".payment..";
    static final String PERMIT_PACKAGE = BASE_PACKAGE + ".permit..";
    static final String REFERENCE_DATA_PACKAGE = BASE_PACKAGE + ".referencedata..";
    static final String TERMS_PACKAGE = BASE_PACKAGE + ".terms..";
    static final String USER_PACKAGE = BASE_PACKAGE + ".user..";
    static final String VERIFICATION_BODY_PACKAGE = BASE_PACKAGE + ".verificationbody..";
    static final String WEB_PACKAGE = BASE_PACKAGE + ".web..";
    static final String WORKFLOW_PACKAGE = BASE_PACKAGE + ".workflow..";

    static final List<String> ALL_PACKAGES = List.of(
            ACCOUNT_PACKAGE,
            AUTHORIZATION_PACKAGE,
            COMMON_PACKAGE,
            FILES_PACKAGE,
            MIGRATION_PACKAGE,
            NOTIFICATION_PACKAGE,
            PAYMENT_PACKAGE,
            PERMIT_PACKAGE,
            REFERENCE_DATA_PACKAGE,
            TERMS_PACKAGE,
            USER_PACKAGE,
            VERIFICATION_BODY_PACKAGE,
            WEB_PACKAGE,
            WORKFLOW_PACKAGE
    );

    @ArchTest
    public static final ArchRule accountPackageChecks =
            noClasses().that()
                    .resideInAPackage(ACCOUNT_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            ACCOUNT_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            COMMON_PACKAGE,
                            VERIFICATION_BODY_PACKAGE,
                            NOTIFICATION_PACKAGE));

    @ArchTest
    public static final ArchRule authorizationPackageChecks =
            noClasses().that()
                    .resideInAPackage(AUTHORIZATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            AUTHORIZATION_PACKAGE,
                            COMMON_PACKAGE,
                            USER_PACKAGE, /* due to UserStatusService*/
                            VERIFICATION_BODY_PACKAGE /* due to VerificationBodyDeletedEventListener */));

    @ArchTest
    public static final ArchRule filesPackageChecks =
            noClasses().that()
                    .resideInAPackage(FILES_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            FILES_PACKAGE,
                            COMMON_PACKAGE,
                            USER_PACKAGE /* for file tokens */));

    @ArchTest
    public static final ArchRule notificationPackageChecks =
            noClasses().that()
                    .resideInAPackage(NOTIFICATION_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            NOTIFICATION_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            FILES_PACKAGE,
                            USER_PACKAGE /* for file tokens */));

    @ArchTest
    public static final ArchRule permitPackageChecks =
            noClasses().that()
                    .resideInAPackage(PERMIT_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            PERMIT_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            ACCOUNT_PACKAGE,
                            FILES_PACKAGE /* for file attachments */,
                            USER_PACKAGE /* for file tokens */));

    @ArchTest
    public static final ArchRule userPackageChecks =
            noClasses().that()
                    .resideInAPackage(USER_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            USER_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE /* to get installation name for notification */,
                            VERIFICATION_BODY_PACKAGE /* for verifier invitation */,
                            FILES_PACKAGE /* for signatures */));

    @ArchTest
    public static final ArchRule workflowPackageChecks =
            noClasses().that()
                    .resideInAPackage(WORKFLOW_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            WORKFLOW_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            NOTIFICATION_PACKAGE,
                            ACCOUNT_PACKAGE,
                            PERMIT_PACKAGE,
                            FILES_PACKAGE,
                            USER_PACKAGE,
                            REFERENCE_DATA_PACKAGE));

    @ArchTest
    public static final ArchRule verificationBodyPackageChecks =
            noClasses().that()
                    .resideInAPackage(VERIFICATION_BODY_PACKAGE)
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(except(
                            VERIFICATION_BODY_PACKAGE,
                            COMMON_PACKAGE,
                            AUTHORIZATION_PACKAGE,
                            USER_PACKAGE));

    private static String[] except(String... packages) {
        return ALL_PACKAGES.stream()
                .filter(p -> !Arrays.asList(packages).contains(p))
                .collect(toList())
                .toArray(String[]::new);
    }
}