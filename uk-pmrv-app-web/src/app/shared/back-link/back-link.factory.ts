import { InjectionToken } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

export const BACK_LINK = new InjectionToken<BehaviorSubject<boolean>>('Back link', {
  providedIn: 'root',
  factory: () => new BehaviorSubject(false),
});

export const BACK_LINK_TARGET = new InjectionToken<BehaviorSubject<{ link: string; fragment: string }>>(
  'Back link target',
  {
    providedIn: 'root',
    factory: () => new BehaviorSubject(null),
  },
);
