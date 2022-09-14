import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-hint',
  template: `
    <p class="govuk-body">Include:</p>
    <ul class="govuk-list govuk-list--bullet">
      <li>the types of instruments used</li>
      <li>if measurements are taken during wet or dry conditions</li>
      <li>the formulae used for applying correction factors (PtO2 and H2O)</li>
      <li>calibration factors required for QAL2 procedures if EN 14181 is applied</li>
      <li>the method used to determine the stoichiometric flue gas volume if flue gas volume is calculated</li>
      <li>
        how annual emissions are determined based on concentration and flue gas flow data, taking time units into
        account
      </li>
    </ul>

    <p class="govuk-body">
      For biomass source streams, describe the calculation used to subtract emissions from biomass from total emissions.
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HintComponent {}
