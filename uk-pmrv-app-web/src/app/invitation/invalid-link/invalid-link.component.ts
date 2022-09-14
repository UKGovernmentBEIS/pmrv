import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

@Component({
  selector: 'app-invalid-link',
  templateUrl: './invalid-link.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidLinkComponent {
  errorCode$ = this.route.queryParamMap.pipe(map((queryParamMap) => queryParamMap.get('code')));

  constructor(private readonly route: ActivatedRoute) {}
}
