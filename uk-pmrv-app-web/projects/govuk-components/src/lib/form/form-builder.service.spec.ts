import { TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';

import { GovukValidators } from '../error-message/govuk-validators';
import { FormBuilderService } from './form-builder.service';

describe('FormBuilderService', () => {
  let service: FormBuilderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: FormBuilder, useClass: FormBuilderService }],
    });
    service = TestBed.inject(FormBuilder);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create groups that update on submission', () => {
    expect(service.group({ name: [] }, { validators: GovukValidators.required('This is required') }).updateOn).toBe(
      'submit',
    );
  });

  it('should allow overriding the updateOn attribute', () => {
    expect(service.group({ name: [] }, { updateOn: 'change' }).updateOn).toBe('change');
  });
});
