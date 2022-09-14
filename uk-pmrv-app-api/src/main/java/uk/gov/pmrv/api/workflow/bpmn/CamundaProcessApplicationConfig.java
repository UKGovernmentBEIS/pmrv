package uk.gov.pmrv.api.workflow.bpmn;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableProcessApplication
@ConditionalOnProperty(name = "camunda.bpm.enabled", havingValue = "true", matchIfMissing = true)
public class CamundaProcessApplicationConfig {

}
