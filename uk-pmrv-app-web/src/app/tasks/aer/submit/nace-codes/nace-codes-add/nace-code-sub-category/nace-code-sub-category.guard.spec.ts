import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { NaceCodeSubCategoryGuard } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.guard';
import { ActivatedRouteSnapshotStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('NaceCodeSubCategoryGuard', () => {
  let guard: NaceCodeSubCategoryGuard;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [HttpClient, HttpHandler, KeycloakService, NaceCodeSubCategoryGuard],
    });
    guard = TestBed.inject(NaceCodeSubCategoryGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow', async () => {
    jest
      .spyOn(router, 'getCurrentNavigation')
      .mockReturnValue({ extras: { state: { mainActivity: 'a main activity' } } } as any);
    expect(guard.canActivate(new ActivatedRouteSnapshotStub())).toEqual(true);
  });

  it('should not allow', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: {} } } as any);
    expect(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: 23 }))).toEqual(
      router.parseUrl('tasks/23/aer/submit/nace-codes/add'),
    );
  });
});
