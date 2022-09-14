import { FormControl } from '@angular/forms';

import { GovukValidators } from './govuk-validators';

describe('GovukValidators', () => {
  it('should create an instance', () => {
    expect(new GovukValidators()).toBeTruthy();
  });

  it('should build a validator with custom message', () => {
    const validator = GovukValidators.builder('This is an error', () => ({ custom: true }));

    expect(validator(new FormControl())).toEqual({ custom: 'This is an error' });
  });

  it('should override angular validators', () => {
    const errorMessage = 'This is an error';

    expect(GovukValidators.required(errorMessage)(new FormControl())).toEqual({ required: errorMessage });
    expect(GovukValidators.requiredTrue(errorMessage)(new FormControl())).toEqual({ required: errorMessage });
    expect(GovukValidators.min(5, errorMessage)(new FormControl(3))).toEqual({ min: errorMessage });
    expect(GovukValidators.max(5, errorMessage)(new FormControl(6))).toEqual({ max: errorMessage });
    expect(GovukValidators.email(errorMessage)(new FormControl('asd'))).toEqual({ email: errorMessage });
    expect(GovukValidators.minLength(5, errorMessage)(new FormControl('long'))).toEqual({ minlength: errorMessage });
    expect(GovukValidators.maxLength(5, errorMessage)(new FormControl('long one'))).toEqual({
      maxlength: errorMessage,
    });
    expect(GovukValidators.pattern('/[0-9]+/', errorMessage)(new FormControl('let'))).toEqual({
      pattern: errorMessage,
    });
  });
});
