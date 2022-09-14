import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-activity-data-template',
  template: ` <p class="govuk-body">Get help with <a [routerLink]="" govukLink>activity data</a>.</p> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityDataTemplateComponent {}
