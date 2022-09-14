package uk.gov.pmrv.api.web.logging;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.spi.StandardLevel;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Collections;
import java.util.List;

/**
 * Class representing application properties with prefix rest.logging.
 */
@ConfigurationProperties(prefix = "rest.logging")
@Getter
@Setter
public class RestLoggingProperties {

    /* List of uri patterns to be excluded from logging. */
    private List<String> excludedUriPatterns = Collections.emptyList();
    private StandardLevel level = StandardLevel.INFO;
}
