import { InjectionToken } from '@angular/core';

export const LOCAL_STORAGE = new InjectionToken<Storage>('Local storage', {
  providedIn: 'root',
  factory: () => window.localStorage,
});
