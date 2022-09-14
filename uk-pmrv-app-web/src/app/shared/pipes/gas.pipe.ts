import { Pipe, PipeTransform } from '@angular/core';

import { RegulatedActivity } from 'pmrv-api';

@Pipe({ name: 'gas' })
export class GasPipe implements PipeTransform {
  transform(value: RegulatedActivity['type']): string {
    switch (value) {
      case 'AMMONIA_PRODUCTION':
      case 'BULK_ORGANIC_CHEMICAL_PRODUCTION':
      case 'CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE':
      case 'CARBON_BLACK_PRODUCTION':
      case 'CEMENT_CLINKER_PRODUCTION':
      case 'CERAMICS_MANUFACTURING':
      case 'COKE_PRODUCTION':
      case 'COMBUSTION':
      case 'FERROUS_METALS_PRODUCTION':
      case 'GLASS_MANUFACTURING':
      case 'GYPSUM_OR_PLASTERBOARD_PRODUCTION':
      case 'HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION':
      case 'LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE':
      case 'MINERAL_OIL_REFINING':
      case 'MINERAL_WOOL_MANUFACTURING':
      case 'NON_FERROUS_METALS_PRODUCTION':
      case 'ORE_ROASTING_OR_SINTERING':
      case 'PAPER_OR_CARDBOARD_PRODUCTION':
      case 'PIG_IRON_STEEL_PRODUCTION':
      case 'PRIMARY_ALUMINIUM_PRODUCTION':
      case 'PULP_PRODUCTION':
      case 'SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION':
      case 'STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE':
      case 'TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE':
        return 'Carbon dioxide';
      case 'NITRIC_ACID_PRODUCTION':
      case 'ADIPIC_ACID_PRODUCTION':
      case 'GLYOXAL_GLYOXYLIC_ACID_PRODUCTION':
        return 'Carbon dioxide and nitrous oxide';
      case 'SECONDARY_ALUMINIUM_PRODUCTION':
        return 'Carbon dioxide and perfluorocarbons';
      default:
        return '';
    }
  }
}
