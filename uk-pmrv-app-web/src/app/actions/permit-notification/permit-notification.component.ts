import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-permit-notification-container',
  template: `<router-outlet></router-outlet>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitNotificationComponent {}
