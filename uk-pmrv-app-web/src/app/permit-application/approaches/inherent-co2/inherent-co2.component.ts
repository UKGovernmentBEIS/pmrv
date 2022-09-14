import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-inherent-co2',
  templateUrl: './inherent-co2.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCO2Component {}
