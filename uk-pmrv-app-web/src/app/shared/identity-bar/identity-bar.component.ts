import { ChangeDetectionStrategy, Component } from '@angular/core';

import { IdentityBarService } from '../../core/services/identity-bar.service';

@Component({
  selector: 'app-identity-bar',
  template: `
    <div class="hmcts-identity-bar" *ngIf="barService.content | async as content">
      <div class="hmcts-identity-bar__container">
        <div class="hmcts-identity-bar__details">
          <div class="hmcts-identity-bar__title" [innerHTML]="content"></div>
        </div>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IdentityBarComponent {
  constructor(public readonly barService: IdentityBarService) {}
}
