import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BackLinkService } from '../../shared/back-link/back-link.service';

@Component({
  selector: 'app-user-registration',
  template: `<router-outlet appSkipLinkFocus></router-outlet>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class UserRegistrationComponent {}
