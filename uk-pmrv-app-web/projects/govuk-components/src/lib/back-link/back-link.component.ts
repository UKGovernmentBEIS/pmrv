import { Location } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'govuk-back-link',
  template: ` <a [routerLink]="" (click)="click()" class="govuk-back-link"> Back </a> `,
})
export class BackLinkComponent {
  @Input() link: string;
  @Input() fragment: string;

  constructor(
    private readonly location: Location,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  click(): void {
    if (!this.link) {
      this.location.back();
    } else {
      this.router.navigate([this.link], {
        relativeTo: this.route,
        ...(this.fragment ? { fragment: this.fragment } : null),
      });
    }
  }
}
