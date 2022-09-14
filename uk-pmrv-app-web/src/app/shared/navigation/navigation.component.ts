import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-navigation',
  template: `
    <div class="hmcts-primary-navigation">
      <div class="hmcts-primary-navigation__container">
        <div class="hmcts-primary-navigation__nav">
          <nav class="hmcts-primary-navigation" [attr.aria-label]="ariaLabel">
            <ul class="hmcts-primary-navigation__list">
              <ng-content></ng-content>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .hmcts-primary-navigation {
        width: 100%;
        margin: 0;
        float: left;
      }

      .hmcts-primary-navigation__container {
        margin: 0;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavigationComponent {
  @Input() ariaLabel = 'Primary navigation';
}
