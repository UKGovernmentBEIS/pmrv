import { Pipe, PipeTransform } from '@angular/core';

import { SourceStream } from 'pmrv-api';

@Pipe({ name: 'sourceStreamDescription' })
export class SourceStreamDescriptionPipe implements PipeTransform {
  transform(value: SourceStream | SourceStream['description']): string {
    if (!value) return '';
    switch (value['description'] ?? value) {
      case 'ACETYLENE':
        return 'Acetylene';
      case 'ANTHRACITE':
        return 'Anthracite';
      case 'BIODIESELS':
        return 'Biodiesels';
      case 'BIOGASOLINE':
        return 'Biogasoline';
      case 'BIOMASS':
        return 'Biomass';
      case 'BITUMEN':
        return 'Bitumen';
      case 'BLAST_FURNACE_GAS':
        return 'Blast Furnace Gas';
      case 'BLENDED_FUEL_GAS':
        return 'Blended Fuel Gas';
      case 'CARBON_MONOXIDE':
        return 'Carbon Monoxide';
      case 'CHARCOAL':
        return 'Charcoal';
      case 'COAL':
        return 'Coal';
      case 'COAL_TAR':
        return 'Coal Tar';
      case 'COKE':
        return 'Coke';
      case 'COKE_OVEN_COKE_LIGNITE_COKE':
        return 'Coke Oven Coke & Lignite Coke';
      case 'COKE_OVEN_GAS':
        return 'Coke Oven Gas';
      case 'COKING_COAL':
        return 'Coking Coal';
      case 'COLLIERY_METHANE':
        return 'Colliery Methane';
      case 'CRUDE_OIL':
        return 'Crude Oil';
      case 'ETHANE':
        return 'Ethane';
      case 'FUEL_GAS':
        return 'Fuel Gas';
      case 'FUEL_OIL':
        return 'Fuel Oil';
      case 'GAS_COKE':
        return 'Gas Coke';
      case 'GAS_DIESEL_OIL':
        return 'Gas/Diesel Oil';
      case 'GAS_OIL':
        return 'Gas/Oil';
      case 'GAS_WORKS':
        return 'Gas Works';
      case 'HIGH_PRESSURE_FLARE_GAS':
        return 'High Pressure Flare Gas';
      case 'IMPORT_FUEL_GAS':
        return 'Import Fuel Gas';
      case 'INDUSTRIAL_WASTES':
        return 'Industrial Wastes';
      case 'KEROSENE_OTHER_THAN_JET_KEROSENE':
        return 'Kerosene (other than jet kerosene)';
      case 'LANDFILL_GAS':
        return 'Landfill Gas';
      case 'LIGNITE':
        return 'Lignite';
      case 'LIQUEFIED_PETROLEUM_GASES':
        return 'Liquefied Petroleum Gases';
      case 'LOW_LOW_PRESSURE_FLARE_GAS':
        return 'Low Low Pressure Flare Gas';
      case 'LOW_PRESSURE_FLARE_GAS':
        return 'Low Pressure Flare Gas';
      case 'LUBRICANTS':
        return 'Lubricants';
      case 'MATERIAL':
        return 'Material';
      case 'MEDIUM_PRESSURE_FLARE_GAS':
        return 'Medium Pressure Flare Gas';
      case 'METHANE':
        return 'Methane';
      case 'MOTOR_GASOLINE':
        return 'Motor Gasoline';
      case 'MSW':
        return 'MSW';
      case 'NAPHTHA':
        return 'Naphtha';
      case 'NATURAL_GAS':
        return 'Natural Gas';
      case 'NATURAL_GAS_LIQUIDS':
        return 'Natural Gas Liquids';
      case 'NON_BIOMASS_PACKAGING_WASTE':
        return 'Non-biomass Packaging Waste';
      case 'OIL_SHALE_AND_TAR_SANDS':
        return 'Oil Shale and Tar Sands';
      case 'OPG':
        return 'OPG';
      case 'ORIMULSION':
        return 'Orimulsion';
      case 'OTHER':
        return value['otherDescriptionName'] || 'Other';
      case 'OTHER_BIOGAS':
        return 'Other Biogas';
      case 'OTHER_BITUMINOUS_COAL':
        return 'Other Bituminous Coal';
      case 'OTHER_LIQUID_BIOFUELS':
        return 'Other Liquid Biofuels';
      case 'OTHER_PETROLEUM_PRODUCTS':
        return 'Other Petroleum Products';
      case 'OTHER_PRIMARY_SOLID_BIOMASS':
        return 'Other Primary Solid Biomass';
      case 'OXYGEN_STEEL_FURNACE_GAS':
        return 'Oxygen Steel Furnace Gas';
      case 'PARAFFIN_WAXES':
        return 'Paraffin Waxes';
      case 'PATENT_FUEL':
        return 'Patent Fuel';
      case 'PEAT':
        return 'Peat';
      case 'PETROL':
        return 'Petrol';
      case 'PETROLEUM_COKE':
        return 'Petroleum Coke';
      case 'PILOT_AND_PURGE_FLARE_GAS':
        return 'Pilot and Purge Flare Gas';
      case 'PILOT_FLARE_GAS':
        return 'Pilot Flare Gas';
      case 'PROPANE':
        return 'Propane';
      case 'REFINERY_FEEDSTOCKS':
        return 'Refinery Feedstocks';
      case 'REFINERY_GAS':
        return 'Refinery Gas';
      case 'RESIDUAL_FUEL_OIL':
        return 'Residual Fuel Oil';
      case 'SCRAP_TYRES':
        return 'Scrap Tyres';
      case 'SHALE_OIL':
        return 'Shale Oil';
      case 'SLUDGE_GAS':
        return 'Sludge Gas';
      case 'SOUR_GAS':
        return 'Sour Gas';
      case 'SOUR_GAS_FLARE':
        return 'Sour Gas Flare';
      case 'SSF':
        return 'SSF';
      case 'SUB_BITUMINOUS_COAL':
        return 'Sub-Bituminous Coal';
      case 'WASTE_OILS':
        return 'Waste Oils';
      case 'WASTE_SOLVENTS':
        return 'Waste Solvents';
      case 'WASTE_TYRES':
        return 'Waste Tyres';
      case 'WHITE_SPIRIT_SBP':
        return 'White Spirit & SBP';
      case 'WOOD_WOOD_WASTE':
        return 'Wood/Wood Waste';
      default:
        return '';
    }
  }
}
