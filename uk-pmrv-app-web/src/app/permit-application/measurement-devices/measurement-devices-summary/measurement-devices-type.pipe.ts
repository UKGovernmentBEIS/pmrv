import { Pipe, PipeTransform } from '@angular/core';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

@Pipe({ name: 'measurementDevicesType' })
export class MeasurementDevicesTypePipe implements PipeTransform {
  transform(value: MeasurementDeviceOrMethod['type']): string {
    switch (value) {
      case 'BALANCE':
        return 'Balance';
      case 'BELLOWS_METER':
        return 'Bellows meter';
      case 'BELT_WEIGHER':
        return 'Belt weigher';
      case 'CORIOLIS_METER':
        return 'Coriolis meter';
      case 'ELECTRONIC_VOLUME_CONVERSION_INSTRUMENT':
        return 'Electronic volume conversion instrument (EVCI)';
      case 'GAS_CHROMATOGRAPH':
        return 'Gas chromatograph';
      case 'LEVEL_GAUGE':
        return 'Level gauge';
      case 'ORIFICE_METER':
        return 'Orifice meter';
      case 'OTHER':
        return 'Other';
      case 'OVALRAD_METER':
        return 'Ovalrad meter';
      case 'ROTARY_METER':
        return 'Rotary meter';
      case 'TANK_DIP':
        return 'Tank dip';
      case 'TURBINE_METER':
        return 'Turbine meter';
      case 'ULTRASONIC_METER':
        return 'Ultrasonic meter';
      case 'VENTURI_METER':
        return 'Venturi meter';
      case 'VORTEX_METER':
        return 'Vortex meter';
      case 'WEIGHBRIDGE':
        return 'Weighbridge';
      case 'WEIGHSCALE':
        return 'Weighscale';
      default:
        return '';
    }
  }
}
