package uk.gov.pmrv.api.common.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Java validation configuration.
 */
@Configuration
public class ValidationConfig {

    /**
     * Add validatorMessages.properties as Spring Message Source.
     *
     * @return {@link MessageSource}
     */
    @Bean
    public MessageSource validationMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:validatorMessages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Attach Spring Message Source to Spring validations.
     *
     * @return {@link LocalValidatorFactoryBean}
     */
    @Bean
    public LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(validationMessageSource());
        return bean;
    }

    /**
     * Attach Spring Message Source to Spring validated method annotation.
     *
     * @return {@link MethodValidationPostProcessor}
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor bean = new MethodValidationPostProcessor();
        bean.setValidatorFactory(getValidator());
        return bean;
    }
}
