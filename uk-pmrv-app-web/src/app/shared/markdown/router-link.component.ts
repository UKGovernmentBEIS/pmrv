import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-router-link',
  template: '<a [routerLink]="href" [fragment]="fragment" govukLink>{{text}}</a>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RouterLinkComponent {
  @Input() href: string;
  @Input() text: string;
  @Input() fragment: string;
}
