import { AddressDTO, LocationDTO } from 'pmrv-api';

import { CompetentAuthority } from '../../shared/interfaces/competent-authority';
import { Coordinates } from '../offshore-details/coordinates';

export type Installation = OnshoreInstallation | OffshoreInstallation;

export interface OnshoreInstallation {
  onshoreGroup: OnshoreDetails;
  locationGroup: InstallationLocation;
  installationTypeGroup: InstallationTypeGroup;
}

export interface OffshoreInstallation {
  offshoreGroup: OffshoreDetails;
  installationTypeGroup: InstallationTypeGroup;
}

export interface OffshoreDetails {
  name: string;
  siteName: string;
  latitude: Coordinates;
  longitude: Coordinates;
}

export interface OnshoreDetails {
  name: string;
  siteName: string;
  address: AddressDTO;
  gridReference: string;
}

export interface InstallationLocation {
  location: CompetentAuthority;
}

export interface InstallationTypeGroup {
  type: LocationDTO['type'];
}
