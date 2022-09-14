import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-decision-confirmation',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <govuk-panel
          title="You've {{ isAccepted ? 'approved' : 'rejected' }} the installation account application"
        ></govuk-panel>
        <h3 class="govuk-heading-m">What happens next</h3>
        <p class="govuk-body">An email will be sent to the relevant user with your decision</p>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DecisionConfirmationComponent {
  @Input() isAccepted: boolean;
}
