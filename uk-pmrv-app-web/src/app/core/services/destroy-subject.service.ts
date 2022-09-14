import { Injectable, OnDestroy } from '@angular/core';

import { ReplaySubject } from 'rxjs';

@Injectable()
export class DestroySubject extends ReplaySubject<void> implements OnDestroy {
  constructor() {
    super(1);
  }

  ngOnDestroy(): void {
    this.next();
    this.complete();
  }
}
