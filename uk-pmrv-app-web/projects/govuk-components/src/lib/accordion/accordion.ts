import { InjectionToken } from '@angular/core';

export interface Accordion {
  id: string;
  openIndexes: number[];
  itemCount: number;
  cacheDisabled: boolean;
}

export const ACCORDION = new InjectionToken<Accordion>('Accordion');

export const accordionFactory = {
  provide: ACCORDION,
  useFactory: () => ({ id: 'accordion', itemCount: 0 }),
};

export const isSessionStorageAvailable: () => boolean = () => {
  let isStorageAvailable = false;
  try {
    sessionStorage.setItem('test', 'test string');
    isStorageAvailable = sessionStorage.getItem('test') === 'test string';
    sessionStorage.removeItem('test');
  } catch (exception) {
    console.error(exception);
  }
  return isStorageAvailable;
};
