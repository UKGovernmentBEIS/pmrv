import { InjectionToken } from '@angular/core';
import {
  AbstractControl,
  AsyncValidatorFn,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';

import { map, Observable } from 'rxjs';

import { AddressInputComponent } from '@shared/address-input/address-input.component';
import { GroupBuilderConfig } from '@shared/types';

import { GovukValidators } from 'govuk-components';

import { AccountsService } from 'pmrv-api';

import { InstallationLocation, InstallationTypeGroup } from '../installation-type/installation';
import { Coordinates } from '../offshore-details/coordinates';

export const INSTALLATION_FORM = new InjectionToken<FormGroup>('Installation form');

export const installationFormFactory = {
  provide: INSTALLATION_FORM,
  useFactory: (fb: FormBuilder, accountsService: AccountsService) =>
    fb.group({
      installationTypeGroup: fb.group(installationTypeGroupControls),
      locationGroup: fb.group(locationGroupControls),
      onshoreGroup: fb.group({
        name: [
          null,
          [
            GovukValidators.required('Enter the installation name'),
            GovukValidators.maxLength(255, 'The installation name should not be more than 255 characters'),
          ],
          [installationNameNotExists(accountsService)],
        ],
        siteName: [
          null,
          [
            GovukValidators.required('Enter the site name'),
            GovukValidators.maxLength(255, 'The site name should not be more than 255 characters'),
          ],
        ],
        ...gridReferenceControl(),
        address: fb.group(AddressInputComponent.controlsFactory(null)),
      }),
      offshoreGroup: fb.group({
        name: [
          null,
          [
            GovukValidators.required('Enter the installation name'),
            GovukValidators.maxLength(255, 'The installation name should not be more than 255 characters'),
          ],
          [installationNameNotExists(accountsService)],
        ],
        siteName: [
          null,
          [
            GovukValidators.required('Enter the site name'),
            GovukValidators.maxLength(255, 'The site name should not be more than 255 characters'),
          ],
        ],
        latitude: fb.group(getLatitudeControls(), {
          validators: [coordinateValidatorFn('Latitude', 90), maxSecondsValidatorFn('Latitude')],
        }),
        longitude: fb.group(getLongitudeControls(), {
          validators: [coordinateValidatorFn('Longitude', 180), maxSecondsValidatorFn('Longitude')],
        }),
      }),
    }),
  deps: [FormBuilder, AccountsService],
};

const installationTypeGroupControls: GroupBuilderConfig<InstallationTypeGroup> = {
  type: [null, GovukValidators.required('Select type of installation')],
};

export const gridReferenceControl = (value?: string) => {
  return {
    gridReference: [
      value ?? null,
      [
        GovukValidators.required('Enter the UK Ordnance Survey grid reference'),
        GovukValidators.builder(
          'Grid reference must consist of two letters and 4-10 numbers (e.g. NN166712 or NN 166 712)',
          (control: AbstractControl) =>
            control.value?.replace(/\s*/g, '').match(/^[a-zA-Z]{2}[0-9]{4,10}\b$/) ? null : { pattern: true },
        ),
      ],
    ],
  };
};

const locationGroupControls: GroupBuilderConfig<InstallationLocation> = {
  location: [null, GovukValidators.required('Select the country that the installation is located in')],
};

export function getLatitudeControls(latitude?: Coordinates): GroupBuilderConfig<Coordinates> {
  return {
    degree: [
      latitude?.degree ?? null,
      [
        GovukValidators.required('Enter latitude degree'),
        GovukValidators.min(0, 'The degree should not be less than 0'),
        GovukValidators.max(90, 'The degree should not be greater than 90'),
        GovukValidators.builder('Degree must be a whole number', integerValidator()),
      ],
    ],
    minute: [
      latitude?.minute ?? null,
      [
        GovukValidators.required('Enter latitude minute'),
        GovukValidators.min(0, 'The minute should not be less than 0'),
        GovukValidators.max(59, 'The minute should not be greater than 59'),
        GovukValidators.builder('Minute must be a whole number', integerValidator()),
      ],
    ],
    second: [
      latitude?.second ?? null,
      [
        GovukValidators.required('Enter latitude second'),
        GovukValidators.min(0, 'The second should not be less than 0'),
        GovukValidators.max(59.99, 'The second should not be greater than 59.99'),
      ],
    ],
    cardinalDirection: [
      latitude?.cardinalDirection ?? null,
      GovukValidators.required('Enter latitude cardinal direction'),
    ],
  };
}

export function getLongitudeControls(longitude?: Coordinates): GroupBuilderConfig<Coordinates> {
  return {
    degree: [
      longitude?.degree ?? null,
      [
        GovukValidators.required('Enter longitude degree'),
        GovukValidators.min(0, 'The degree should not be less than 0'),
        GovukValidators.max(180, 'The degree should not be greater than 180'),
        GovukValidators.builder('Degree must be a whole number', integerValidator()),
      ],
    ],
    minute: [
      longitude?.minute ?? null,
      [
        GovukValidators.required('Enter longitude minute'),
        GovukValidators.min(0, 'The minute should not be less than 0'),
        GovukValidators.max(59, 'The minute should not be greater than 59'),
        GovukValidators.builder('Minute must be a whole number', integerValidator()),
      ],
    ],
    second: [
      longitude?.second ?? null,
      [
        GovukValidators.required('Enter longitude second'),
        GovukValidators.min(0, 'The second should not be less than 0'),
        GovukValidators.max(59.99, 'The second should not be greater than 59.99'),
      ],
    ],
    cardinalDirection: [
      longitude?.cardinalDirection ?? null,
      GovukValidators.required('Enter longitude cardinal direction'),
    ],
  };
}

export function coordinateValidatorFn(groupName: string, maxDegree: number): ValidatorFn {
  return (group: FormGroup): ValidationErrors => {
    const degree = group.get('degree').value;
    const minute = group.get('minute').value;
    const second = group.get('second').value;

    return degree > maxDegree || (degree === maxDegree && (minute > 0 || second > 0))
      ? { invalidDegree: `${groupName} should not be greater than ${maxDegree}° 0' 0"` }
      : null;
  };
}

export function maxSecondsValidatorFn(groupName: string): ValidatorFn {
  return (group: FormGroup): ValidationErrors => {
    const minute = group.get('minute').value;
    const second = group.get('second').value;

    return minute * 60 + second > 3600
      ? { invalidSeconds: `${groupName}'s seconds summary should not be greater than 3600` }
      : null;
  };
}

function integerValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: boolean } | null => {
    const inputNumValue = Number(control.value);

    return isNaN(inputNumValue) || Number.isInteger(inputNumValue) ? null : { invalidInteger: true };
  };
}

function installationNameNotExists(accountsService: AccountsService): AsyncValidatorFn {
  return (control: AbstractControl): Observable<ValidationErrors | null> =>
    accountsService
      .isExistingAccountNameUsingGET(control.value)
      .pipe(
        map((res) =>
          res ? { installationNameExists: 'The installation name already exists. Enter a new name.' } : null,
        ),
      );
}
