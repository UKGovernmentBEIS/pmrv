import { AbstractControl, ValidatorFn, Validators } from '@angular/forms';

import { MessageValidationErrors, MessageValidatorFn } from './message-validation-errors';

// @dynamic
export class GovukValidators {
  static builder(message: string | MessageValidationErrors, validator: ValidatorFn): MessageValidatorFn {
    const validatorFn: MessageValidatorFn = (control: AbstractControl) => {
      const validity = validator(control);

      return validity
        ? Object.keys(validity).reduce(
            (errors, key) => ({ ...errors, [key]: typeof message === 'string' ? message : message[key] }),
            {},
          )
        : null;
    };
    return validatorFn;
  }

  static required(message: string): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.required);
  }

  static requiredTrue(message: string): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.requiredTrue);
  }

  static min(min: number, message: string): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.min(min));
  }

  static max(max: number, message: string): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.max(max));
  }

  static email(message = 'Enter a valid email'): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.email);
  }

  static minLength(length: number, message: string): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.minLength(length));
  }

  static maxLength(length: number, message: string): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.maxLength(length));
  }

  static pattern(pattern: string | RegExp, message: string): MessageValidatorFn {
    return GovukValidators.builder(message, Validators.pattern(pattern));
  }

  static maxFileSize(maxFileSize: number, message: string): MessageValidatorFn {
    return GovukValidators.builder(message, this.maxFileSizeValidator(maxFileSize));
  }

  static fileExtension(accepted: string, message: string): MessageValidatorFn {
    const stringArray = accepted.replace(/\./g, '').split(',');
    return GovukValidators.builder(message, this.fileExtensionValidator(stringArray));
  }

  static notNaN(message: string): MessageValidatorFn {
    return GovukValidators.builder(message, this.notNaNValidator());
  }

  static empty(message: string): MessageValidatorFn {
    return GovukValidators.builder(message, this.emptyValidator());
  }

  static incomplete(message: string): MessageValidatorFn {
    return GovukValidators.builder(message, this.incompleteValidator());
  }

  static positiveNumber(message = 'Must accept only positive values >0'): MessageValidatorFn {
    return GovukValidators.builder(message, this.isPositiveNumber());
  }

  private static isPositiveNumber(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      const inputNumValue = Number(control.value);

      return control.value && inputNumValue <= 0 ? { invalidPositive: true } : null;
    };
  }

  private static maxFileSizeValidator(maxFileSize: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (
        control.value &&
        control.value instanceof FileList &&
        Object.values(control.value).some((file) => file.size > maxFileSize)
      ) {
        return { maxFileSize: true };
      }
      return null;
    };
  }

  private static fileExtensionValidator(accepted: string[]): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (
        control.value &&
        control.value instanceof FileList &&
        Object.values(control.value).some((file) => accepted.indexOf(file.name.split('.').pop()) === -1)
      ) {
        return { fileExtension: true };
      }
      return null;
    };
  }

  private static notNaNValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (control.value !== '' && isNaN(control.value)) {
        return { isNaN: true };
      }
      return null;
    };
  }

  private static emptyValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (
        control.value === null ||
        control.value === undefined ||
        Object.keys(control.value).length === 0 ||
        Object.keys(control.value).every(
          (key) => control.value[key] === null || control.value[key] === undefined || control.value[key] === '',
        )
      ) {
        return { empty: true };
      }
      return null;
    };
  }

  private static incompleteValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null => {
      if (
        control.value !== null &&
        control.value !== undefined &&
        Object.keys(control.value).length !== 0 &&
        Object.keys(control.value).some(
          (key) => control.value[key] === null || control.value[key] === undefined || control.value[key] === '',
        ) &&
        Object.keys(control.value).some(
          (key) => control.value[key] !== null && control.value[key] !== undefined && control.value[key] !== '',
        )
      ) {
        return { incomplete: true };
      }
      return null;
    };
  }
}
