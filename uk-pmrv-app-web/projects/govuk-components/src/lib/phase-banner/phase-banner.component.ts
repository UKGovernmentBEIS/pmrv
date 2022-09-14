import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-phase-banner',
  templateUrl: './phase-banner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .align-right {
        display: flex;
        justify-content: space-between;
      }
      .order-2 {
        order: 2;
      }
    `,
  ],
})
export class PhaseBannerComponent {
  @Input() phase: string;
  @Input() tagColor: string;
  @Input() tagAlign: 'right' | 'left' = 'left';
}
