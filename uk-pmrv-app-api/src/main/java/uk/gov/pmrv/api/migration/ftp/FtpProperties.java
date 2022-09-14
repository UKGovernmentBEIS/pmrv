package uk.gov.pmrv.api.migration.ftp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.migration.MigrationEndpoint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Validated
@Component
@ConfigurationProperties(prefix = "migration-ftp")
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Getter
@Setter
public class FtpProperties {

    @NotBlank
    private String url;
    
    @NotBlank
    private String username;
    
    @Positive
    private int port;

    private Resource keyPath;
}
