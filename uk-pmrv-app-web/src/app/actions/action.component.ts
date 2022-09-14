import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { PRIMARY_OUTLET, Router } from '@angular/router';

import { DestroySubject } from '../core/services/destroy-subject.service';

@Component({
  selector: 'app-actions-component',
  template: `<router-outlet></router-outlet>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ActionComponent implements OnInit {
  constructor(private readonly router: Router) {}

  ngOnInit(): void {
    const url = this.router.parseUrl(this.router.url);
    const urlSegments = url.root.children[PRIMARY_OUTLET].segments;
    if (urlSegments.length == 1) {
      this.router.navigate(['/dashboard']);
    }
  }
}
