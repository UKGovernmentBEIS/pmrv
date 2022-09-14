import { FormControl } from '@angular/forms';

import { MessageValidationErrors, MessageValidatorFn } from 'govuk-components';

import { FileValidators } from './file-validators';

describe('FileValidators', () => {
  it('should create an instance', () => {
    expect(new FileValidators()).toBeTruthy();
  });

  it('should concatenate the errors of several files', () => {
    const errors: MessageValidationErrors[] = [
      {
        upload: 'An upload has failed',
        test: 'A test has failed',
        random: 'A random error',
      },
      {
        upload: 'Another upload has failed',
      },
    ];

    expect(FileValidators.concatenateErrors(errors)).toEqual({
      'upload-0': 'An upload has failed',
      'test-0': 'A test has failed',
      'random-0': 'A random error',
      'upload-1': 'Another upload has failed',
    });
  });

  it('should apply a single file validator to multiple files', () => {
    const validator: MessageValidatorFn = (control) =>
      control.value.uuid ? null : { required: 'The file is required' };
    const formControl = new FormControl();
    formControl.setValue([{ uuid: 1 }, { uuid: null }]);

    expect(FileValidators.multipleCompose(validator)(formControl)).toEqual({ 'required-1': 'The file is required' });
  });

  it('should limit the file size to given amount', () => {
    const formControl = new FormControl(
      { file: { name: 'Test file', size: 1024 * 1024 } },
      { validators: FileValidators.maxFileSize(2) },
    );

    expect(formControl.errors).toBeNull();
    expect(formControl.valid).toBeTruthy();

    formControl.setValue({ file: { name: 'Test file', size: 3 * 1024 * 1024 } });
    expect(formControl.errors).toEqual({ ['maxFileSize-0']: 'Test file must be smaller than 2MB' });
    expect(formControl.invalid).toBeTruthy();
  });
});
