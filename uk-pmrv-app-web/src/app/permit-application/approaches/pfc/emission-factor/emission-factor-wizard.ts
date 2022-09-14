import { PFCTier2EmissionFactor } from 'pmrv-api';

export function isWizardComplete(emissionFactor: PFCTier2EmissionFactor): boolean {
  return !!emissionFactor && (emissionFactor?.exist ?? true) === true
    ? !!emissionFactor?.determinationInstallation && !!emissionFactor?.scheduleMeasurements
    : emissionFactor?.exist != undefined;
}
