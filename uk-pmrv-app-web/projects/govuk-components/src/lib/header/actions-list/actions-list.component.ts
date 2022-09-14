import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'govuk-header-actions-list',
  template: `
    <div class="uk-pmrv-header-actions">
      <ng-content></ng-content>
    </div>
  `,
  styles: [
    `
      .uk-pmrv-header-actions {
        float: right;
      }
      .uk-pmrv-header-actions a {
        margin-left: 1em;
        line-height: 2em;
        cursor: pointer;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  // eslint-disable-next-line @angular-eslint/use-component-view-encapsulation
  encapsulation: ViewEncapsulation.None,
})
export class HeaderActionsListComponent {}
