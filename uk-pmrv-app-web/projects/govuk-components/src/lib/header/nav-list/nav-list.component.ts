import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'govuk-header-nav-list',
  templateUrl: './nav-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderNavListComponent {
  @Input() identifier = 'navigation';
  @Input() ariaLabel = 'Navigation menu';
  @Input() menuButtonAriaLabel = 'Show or hide navigation menu';

  isNavigationOpen = false;
}
