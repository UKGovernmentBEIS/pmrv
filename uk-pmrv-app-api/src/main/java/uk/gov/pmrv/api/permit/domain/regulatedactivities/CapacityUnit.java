package uk.gov.pmrv.api.permit.domain.regulatedactivities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CapacityUnit {

    MW_TH("MW(th)"),
    KW_TH("kW(th)"),
    MVA("MVA"),
    KVA("kVA"),
    KW("kW"),
    MW("MW"),
    TONNES_PER_DAY("tonnes/day"),
    TONNES_PER_HOUR("tonnes/hour"),
    TONNES_PER_ANNUM("tonnes/annum"),
    KG_PER_DAY("kg/day"),
    KG_PER_HOUR("kg/hour");
    
    private String description;
}
