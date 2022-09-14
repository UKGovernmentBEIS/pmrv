import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { ConfirmSubmitGuard } from './confirm-submit-guard';

describe('Confirm Submit Guard', () => {
  let router: Router;
  let guard: ConfirmSubmitGuard;
  let store: PermitRevocationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 13 };
  activatedRouteSnapshot.data = { statusKey: 'REVOCATION_APPLY' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(ConfirmSubmitGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitRevocationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return to summary page when section status is completed', async () => {
    store.setState({
      ...mockTaskState,
      isEditable: true,
      cessation: {
        determinationOutcome: 'APPROVED',
        annualReportableEmissions: 22,
        subsistenceFeeRefunded: false,
        noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
        notes: 'my notes'
      },
      cessationCompleted: true,
    });

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/cessation/confirm/summary`),
    );
  });

  it('should return permit cessation', async () => {
    store.setState(mockTaskState);

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/cessation`),
    );
  });

  it('should return true if permit cessation is completed', async () => {
    store.setState({
      ...mockTaskState,
      isEditable: false,
      cessation: {
        determinationOutcome: 'APPROVED',
        annualReportableEmissions: 22,
        subsistenceFeeRefunded: false,
        noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
        notes: 'my notes'
      },
    });

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      true,
    );
  });
});
