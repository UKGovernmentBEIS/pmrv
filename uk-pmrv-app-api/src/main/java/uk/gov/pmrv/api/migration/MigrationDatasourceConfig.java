package uk.gov.pmrv.api.migration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationDatasourceConfig {

    @Bean
    @ConfigurationProperties(prefix="migration-datasource")
    public DataSourceProperties migrationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "migration-datasource.hikari")
    public HikariDataSource migrationDataSource() {
        return migrationDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    public JdbcTemplate migrationJdbcTemplate() {
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(migrationDataSource());
        return template;
    }
}
