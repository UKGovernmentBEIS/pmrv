import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-related-content',
  template: `
    <aside class="app-related-items" role="complementary">
      <h2 class="govuk-heading-m" id="subsection-title">{{ header }}</h2>
      <nav role="navigation" aria-labelledby="subsection-title">
        <ng-content></ng-content>
      </nav>
    </aside>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RelatedContentComponent {
  @Input() header = 'Related content';
}
