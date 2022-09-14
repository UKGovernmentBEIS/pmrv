package uk.gov.pmrv.api.permit.domain.regulatedactivities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RegulatedActivitySection {
    
    COMBUSTION,
    REFINING,
    METALS,
    MINERALS,
    GLASS_AND_MINERAL_WOOL,
    PULP_AND_PAPER,
    CHEMICALS,
    CARBON_CAPTURE_AND_STORAGE
    
}
