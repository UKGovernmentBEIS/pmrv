import { CoordinatesDTO } from 'pmrv-api';

export interface Coordinates {
  degree: number;
  minute: number;
  second: number;
  cardinalDirection: CoordinatesDTO['cardinalDirection'];
}
