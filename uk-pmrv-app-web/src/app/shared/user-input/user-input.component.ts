import { Component, Input } from '@angular/core';

import { existingControlContainer } from '../providers/control-container.factory';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-user-input',
  templateUrl: './user-input.component.html',
  viewProviders: [existingControlContainer],
})
export class UserInputComponent {
  @Input() phoneType: 'full' | 'national';
}
