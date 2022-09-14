import { Pipe, PipeTransform } from '@angular/core';

import { RegulatedActivity } from 'pmrv-api';

@Pipe({ name: 'regulatedActivityType' })
export class RegulatedActivityTypePipe implements PipeTransform {
  transform(value: RegulatedActivity['type'] | 'excludedRegulatedActivity'): string {
    switch (value) {
      case 'ADIPIC_ACID_PRODUCTION':
        return 'Adipic acid production';
      case 'AMMONIA_PRODUCTION':
        return 'Ammonia production';
      case 'BULK_ORGANIC_CHEMICAL_PRODUCTION':
        return 'Bulk organic chemical production';
      case 'CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE':
        return 'Capture of greenhouse gases under Directive 2009/31/EC';
      case 'CARBON_BLACK_PRODUCTION':
        return 'Carbon black production';
      case 'CEMENT_CLINKER_PRODUCTION':
        return 'Cement clinker production';
      case 'CERAMICS_MANUFACTURING':
        return 'Ceramics manufacturing';
      case 'COKE_PRODUCTION':
        return 'Coke production';
      case 'COMBUSTION':
        return 'Combustion';
      case 'FERROUS_METALS_PRODUCTION':
        return 'Ferrous metals production or processing';
      case 'GLASS_MANUFACTURING':
        return 'Glass manufacturing';
      case 'GLYOXAL_GLYOXYLIC_ACID_PRODUCTION':
        return 'Glyoxal and glyoxylic acid production';
      case 'GYPSUM_OR_PLASTERBOARD_PRODUCTION':
        return 'Gypsum or plasterboard production or processing';
      case 'HYDROGEN_AND_SYNTHESIS_GAS_PRODUCTION':
        return 'Hydrogen and synthesis gas production';
      case 'LIME_OR_CALCINATION_OF_DOLOMITE_OR_MAGNESITE':
        return 'Lime or calcination of dolomite or magnesite';
      case 'MINERAL_OIL_REFINING':
        return 'Mineral oil refining';
      case 'MINERAL_WOOL_MANUFACTURING':
        return 'Mineral wool manufacturing';
      case 'NITRIC_ACID_PRODUCTION':
        return 'Nitric acid production';
      case 'NON_FERROUS_METALS_PRODUCTION':
        return 'Non-ferrous metals production or processing';
      case 'ORE_ROASTING_OR_SINTERING':
        return 'Metal ore roasting or sintering';
      case 'PAPER_OR_CARDBOARD_PRODUCTION':
        return 'Paper or cardboard production';
      case 'PIG_IRON_STEEL_PRODUCTION':
        return 'Pig iron or steel production';
      case 'PRIMARY_ALUMINIUM_PRODUCTION':
        return 'Primary aluminium production';
      case 'PULP_PRODUCTION':
        return 'Pulp production';
      case 'SECONDARY_ALUMINIUM_PRODUCTION':
        return 'Secondary aluminium production';
      case 'SODA_ASH_AND_SODIUM_BICARBONATE_PRODUCTION':
        return 'Soda ash (Na2CO3) and sodium bicarbonate (NaHCO3) production';
      case 'STORAGE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE':
        return 'Storage of greenhouse gases under Directive 2009/31/EC';
      case 'TRANSPORT_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE':
        return 'Transport of greenhouse gases under Directive 2009/31/EC';
      case 'excludedRegulatedActivity':
        return 'This emission summary is an excluded activity';
      default:
        return '';
    }
  }
}
