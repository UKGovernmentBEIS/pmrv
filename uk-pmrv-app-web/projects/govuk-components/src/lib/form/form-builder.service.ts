import { Injectable } from '@angular/core';
import { AbstractControlOptions, FormBuilder, FormGroup } from '@angular/forms';

@Injectable()
export class FormBuilderService extends FormBuilder {
  group(controlsConfig: { [p: string]: any }, options?: AbstractControlOptions | null): FormGroup {
    return super.group(controlsConfig, { updateOn: 'submit', ...options });
  }
}
