package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PermitVariationModificationType {

	COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE(false),
	INSTALLATION_NAME(false),
	REGISTERED_OFFICE_ADDRESS(false),
	INSTALLATION_ADDRESS(false),
	GRID_REFERENCE(false),
	METER_RENAMING(false),
	METER_LOCATION_DESCRIPTION(false),
	RELEASE_EMISSION_POINT_DESCRIPTION(false),
	OTHER_NON_SIGNFICANT(true),
	
	INSTALLATION_CATEGORY(false),
	NOTWITHSTANDING_ARTICLE_47_8(false),
	EMISSION_SOURCES(false),
	CALCULATION_TO_MEASUREMENT_METHODOLOGIES(false),
	TIER_APPLIED(false),
	NEW_SOURCE_STREAMS(false),
	SOURCE_STREAMS_CATEGORISATION(false),
	METHODS(false),
	QUANTIFICATION_METHODOLOGY_FOR_EMISSIONS(false),
	OTHER_MONITORING_PLAN(true),
	
	INSTALLATION_SUB(false),
	MONITORING_REPORT_METHODOLOGY_4_4_OR_4_6(false),
	DEFAULT_VALUE_OR_ESTIMATION_METHOD(false),
	COMPETENT_AUTHORITY(false),
	OTHER_MONITORING_METHODOLOGY_PLAN(true)
	;
	
	private boolean otherSummaryApplies;
}
