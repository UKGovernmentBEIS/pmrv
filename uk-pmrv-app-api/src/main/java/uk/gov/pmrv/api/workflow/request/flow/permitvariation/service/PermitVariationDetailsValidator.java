package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationDetails;

@Service
@Validated
public class PermitVariationDetailsValidator {

	public void validate(@Valid @NotNull PermitVariationDetails permitVariationDetails) {};
}
