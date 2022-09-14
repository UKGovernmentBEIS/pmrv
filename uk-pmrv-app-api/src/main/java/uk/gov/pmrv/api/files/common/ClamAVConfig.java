package uk.gov.pmrv.api.files.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import xyz.capybara.clamav.ClamavClient;

@Configuration
public class ClamAVConfig {

    @Bean
    public ClamavClient clamavClient(AppProperties appProperties) {
        return new ClamavClient(appProperties.getClamAV().getHost(), appProperties.getClamAV().getPort());
    }
}
