package uk.gov.pmrv.api.common;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfiguration {

    @Bean
    public Clock configureClock() {
        return Clock.systemUTC();
    }

}
