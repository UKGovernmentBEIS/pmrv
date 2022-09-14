import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-confirmation-shared',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel [title]="title"></govuk-panel>
        <p class="govuk-body">Notification text if needed (TBD)</p>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p class="govuk-body">Explanation text(TBD)</p>
        <a govukLink routerLink="/dashboard"> Return to dashboard </a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmationComponent {
  @Input() title: string;
}
