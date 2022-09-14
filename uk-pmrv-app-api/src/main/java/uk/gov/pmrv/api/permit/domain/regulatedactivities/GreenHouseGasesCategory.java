package uk.gov.pmrv.api.permit.domain.regulatedactivities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GreenHouseGasesCategory {
    
    CARBON_DIOXIDE("Carbon dioxide"),
    CARBON_DIOXIDE_AND_PERFUOROCARBONS("Carbon dioxide and perfluorocarbons"),
    CARBON_DIOXIDE_AND_NITROUS_OXIDE("Carbon dioxide and nitrous oxide")
    ;
    
    private String description;
}
