import { forwardRef, Injectable } from '@angular/core';
import { NgControl } from '@angular/forms';

/* eslint-disable-next-line @angular-eslint/no-forward-ref */
@Injectable({ providedIn: 'root', useClass: forwardRef(() => DotFormService) })
export abstract class FormService {
  getControlIdentifier(control: NgControl): string {
    return control.path ? this.getIdentifier(control.path) : null;
  }

  abstract getIdentifier(path: string[]): string;
}

/* eslint-disable-next-line @angular-eslint/use-injectable-provided-in */
@Injectable()
export class DotFormService extends FormService {
  getIdentifier(path: string[]): string {
    return path.join('.');
  }
}
