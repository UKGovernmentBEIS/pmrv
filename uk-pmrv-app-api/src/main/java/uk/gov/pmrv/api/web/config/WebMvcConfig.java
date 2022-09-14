package uk.gov.pmrv.api.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final PmrvUserArgumentResolver pmrvUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(pmrvUserArgumentResolver);
    }
}
