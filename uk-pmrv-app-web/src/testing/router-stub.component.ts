import { ChangeDetectionStrategy, Component, NgZone } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

@Component({
  selector: 'app-router-stub',
  template: '',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RouterStubComponent {}

export const expectToHaveNavigatedTo = (route: string) => {
  expect(TestBed.inject(Router).isActive(route, true)).toBeTruthy();
};

export const runNavigateInZone: () => void = () => {
  const router = TestBed.inject(Router);
  const originalNavigateByUrl = router.navigateByUrl;
  jest
    .spyOn(router, 'navigateByUrl')
    .mockImplementation((...options) => TestBed.inject(NgZone).run(() => originalNavigateByUrl.apply(router, options)));
};
