import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export interface MessageValidationErrors extends ValidationErrors {
  [key: string]: string;
}

export declare interface MessageValidatorFn extends ValidatorFn {
  (control: AbstractControl): MessageValidationErrors | null;
}
