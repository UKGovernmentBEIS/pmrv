import { Pipe, PipeTransform } from '@angular/core';

import { SourceStream } from 'pmrv-api';

@Pipe({ name: 'sourceStreamType' })
export class SourceStreamTypePipe implements PipeTransform {
  transform(value: SourceStream['type']): string {
    switch (value) {
      case 'AMMONIA_FUEL_AS_PROCESS_INPUT':
        return 'Ammonia: Fuel as process input';
      case 'BULK_ORGANIC_CHEMICALS_MASS_BALANCE_METHODOLOGY':
        return 'Bulk organic chemicals: Mass balance methodology';
      case 'CARBON_BLACK_MASS_BALANCE_METHODOLOGY':
        return 'Carbon black: Mass balance methodology';
      case 'CEMENT_CLINKER_CKD':
        return 'Cement clinker: CKD';
      case 'CEMENT_CLINKER_CLINKER_OUTPUT_METHOD_B':
        return 'Cement clinker: Clinker output (Method B)';
      case 'CEMENT_CLINKER_KILN_INPUT_BASED_METHOD_A':
        return 'Cement clinker: Kiln input based (Method A)';
      case 'CEMENT_CLINKER_NON_CARBONATE_CARBON':
        return 'Cement clinker: Non-carbonate carbon';
      case 'CERAMICS_ALKALI_OXIDE_METHOD_B':
        return 'Ceramics: Alkali oxide (Method B)';
      case 'CERAMICS_CARBON_INPUTS_METHOD_A':
        return 'Ceramics: Carbon inputs (Method A)';
      case 'CERAMICS_SCRUBBING':
        return 'Ceramics: Scrubbing';
      case 'COKE_CARBONATE_INPUT_METHOD_A':
        return 'Coke: Carbonate input (Method A)';
      case 'COKE_FUEL_AS_PROCESS_INPUT':
        return 'Coke: Fuel as process input';
      case 'COKE_MASS_BALANCE':
        return 'Coke: Mass balance';
      case 'COKE_OXIDE_OUTPUT_METHOD_B':
        return 'Coke: Oxide output (Method B)';
      case 'COMBUSTION_COMMERCIAL_STANDARD_FUELS':
        return 'Combustion: Commercial standard fuels';
      case 'COMBUSTION_FLARES':
        return 'Combustion: Flares';
      case 'COMBUSTION_GAS_PROCESSING_TERMINALS':
        return 'Combustion: Gas Processing Terminals';
      case 'COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS':
        return 'Combustion: Other gaseous & liquid fuels';
      case 'COMBUSTION_SCRUBBING_CARBONATE':
        return 'Combustion: Scrubbing (carbonate)';
      case 'COMBUSTION_SCRUBBING_GYPSUM':
        return 'Combustion: Scrubbing (gypsum)';
      case 'COMBUSTION_SOLID_FUELS':
        return 'Combustion: Solid fuels';
      case 'GLASS_AND_MINERAL_WOOL_CARBONATES_INPUT':
        return 'Glass and mineral wool: Carbonates (input)';
      case 'HYDROGEN_AND_SYNTHESIS_GAS_FUEL_AS_PROCESS_INPUT':
        return 'Hydrogen and synthesis gas: Fuel as process input';
      case 'HYDROGEN_AND_SYNTHESIS_GAS_MASS_BALANCE_METHODOLOGY':
        return 'Hydrogen and synthesis gas: Mass balance methodology';
      case 'IRON_STEEL_CARBONATE_INPUT':
        return 'Iron & steel: Carbonate input';
      case 'IRON_STEEL_FUEL_AS_PROCESS_INPUT':
        return 'Iron & steel: Fuel as process input';
      case 'IRON_STEEL_MASS_BALANCE':
        return 'Iron & steel: Mass balance';
      case 'LIME_DOLOMITE_MAGNESITE_ALKALI_EARTH_OXIDE_METHOD_B':
        return 'Lime / dolomite / magnesite: Alkali earth oxide (Method B)';
      case 'LIME_DOLOMITE_MAGNESITE_CARBONATES_METHOD_A':
        return 'Lime / dolomite / magnesite: Carbonates (Method A)';
      case 'LIME_DOLOMITE_MAGNESITE_KILN_DUST_METHOD_B':
        return 'Lime / dolomite / magnesite: Kiln dust (Method B)';
      case 'METAL_ORE_CARBONATE_INPUT':
        return 'Metal ore: Carbonate input';
      case 'METAL_ORE_MASS_BALANCE':
        return 'Metal ore: Mass balance';
      case 'NON_FERROUS_SEC_ALUMINIUM_MASS_BALANCE_METHODOLOGY':
        return 'Non ferrous, sec. aluminium: Mass balance methodology';
      case 'NON_FERROUS_SEC_ALUMINIUM_PROCESS_EMISSIONS':
        return 'Non ferrous, sec. aluminium: Process emissions';
      case 'OTHER':
        return 'Other';
      case 'PRIMARY_ALUMINIUM_MASS_BALANCE_METHODOLOGY':
        return 'Primary aluminium: Mass balance methodology';
      case 'PRIMARY_ALUMINIUM_PFC_EMISSIONS_OVERVOLTAGE_METHOD':
        return 'Primary aluminium: PFC emissions (overvoltage method)';
      case 'PRIMARY_ALUMINIUM_PFC_EMISSIONS_SLOPE_METHOD':
        return 'Primary aluminium: PFC emissions (slope method)';
      case 'PULP_PAPER_MAKE_UP_CHEMICALS':
        return 'Pulp & paper: Make up chemicals';
      case 'REFINERIES_CATALYTIC_CRACKER_REGENERATION':
        return 'Refineries: Catalytic cracker regeneration';
      case 'REFINERIES_HYDROGEN_PRODUCTION':
        return 'Refineries: Hydrogen production';
      case 'REFINERIES_MASS_BALANCE':
        return 'Refineries: Mass balance';
      case 'SODA_ASH_SODIUM_BICARBONATE_MASS_BALANCE_METHODOLOGY':
        return 'Soda ash / sodium bicarbonate: Mass balance methodology';
      default:
        return '';
    }
  }
}
