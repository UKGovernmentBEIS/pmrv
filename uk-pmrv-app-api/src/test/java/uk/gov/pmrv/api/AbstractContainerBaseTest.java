package uk.gov.pmrv.api;

import java.time.Duration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerBaseTest {

    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;
    static final PostgreSQLContainer<?> POSTGRESQL_MIGRATION_CONTAINER;

    static {
		POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres")
			.withDatabaseName("pmrv-docker-tests-db")
			.withPassword("inmemory")
			.withUsername("inmemory")
			.withExposedPorts(5444)
			.withStartupTimeout(Duration.ofMinutes(2));


		POSTGRESQL_MIGRATION_CONTAINER = new PostgreSQLContainer<>("postgres")
                .withDatabaseName("pmrv-docker-migration-tests-db")
                .withPassword("inmemory")
                .withUsername("inmemory")
                .withExposedPorts(5445)
			.withStartupTimeout(Duration.ofMinutes(2))
		;
    	
    	POSTGRESQL_CONTAINER.start();
    	POSTGRESQL_MIGRATION_CONTAINER.start();
    }
    
    @DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
		registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
		
		registry.add("migration-datasource.url", POSTGRESQL_MIGRATION_CONTAINER::getJdbcUrl);
        registry.add("migration-datasource.password", POSTGRESQL_MIGRATION_CONTAINER::getPassword);
        registry.add("migration-datasource.username", POSTGRESQL_MIGRATION_CONTAINER::getUsername);
	}
}
