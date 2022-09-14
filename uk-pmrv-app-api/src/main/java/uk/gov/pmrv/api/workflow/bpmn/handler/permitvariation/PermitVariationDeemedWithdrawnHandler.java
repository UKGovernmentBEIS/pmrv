package uk.gov.pmrv.api.workflow.bpmn.handler.permitvariation;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermitVariationDeemedWithdrawnHandler implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//TODO
	}

}
