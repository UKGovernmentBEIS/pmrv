import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-two-fa-link',
  template: `
    <div class="govuk-button-group">
      <a govukLink routerLink="/2fa/change">Change two factor authentication</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TwoFaLinkComponent {}
