package uk.gov.pmrv.api.notification.template;

import fr.opensagres.xdocreport.document.docx.discovery.DocxTemplateEngineConfiguration;
import fr.opensagres.xdocreport.template.freemarker.FreemarkerTemplateEngine;
import freemarker.core.TemplateClassResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static freemarker.template.Configuration.VERSION_2_3_31;

@Configuration
public class TemplatesConfiguration {

    @Bean
    public freemarker.template.Configuration freemarkerConfig() {
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(VERSION_2_3_31);
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
        return configuration;
    }

    @Bean
    public FreemarkerTemplateEngine freemarkerTemplateEngine(freemarker.template.Configuration freemarkerConfig) {
        FreemarkerTemplateEngine freemarkerTemplateEngine = new FreemarkerTemplateEngine();
        freemarkerTemplateEngine.setFreemarkerConfiguration(freemarkerConfig);
        freemarkerTemplateEngine.setConfiguration( DocxTemplateEngineConfiguration.INSTANCE );
        return freemarkerTemplateEngine;
    }
}
