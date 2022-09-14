import { Inject, Injectable, OnDestroy } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

import { BREADCRUMB_ITEMS } from './breadcrumb.factory';
import { BreadcrumbItem } from './breadcrumb.interface';

@Injectable()
export class BreadcrumbService implements OnDestroy {
  constructor(@Inject(BREADCRUMB_ITEMS) private readonly breadcrumbItem$: BehaviorSubject<BreadcrumbItem[]>) {}

  show(items: BreadcrumbItem[]): void {
    this.breadcrumbItem$.next(items);
  }

  clear(): void {
    this.breadcrumbItem$.next(null);
  }

  ngOnDestroy(): void {
    this.breadcrumbItem$.next(null);
  }
}
