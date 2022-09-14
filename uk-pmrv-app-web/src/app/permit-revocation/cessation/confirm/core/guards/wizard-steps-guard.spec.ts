import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { WizardStepsGuard } from './wizard-steps-guard';

describe('Wizard Steps Guard', () => {
  let router: Router;
  let guard: WizardStepsGuard;
  let store: PermitRevocationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 13 };
  activatedRouteSnapshot.data = { keys: ['allowancesSurrenderDate'] };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(WizardStepsGuard);
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
        notes: 'my notes',
      },
      allowancesSurrenderRequired: true,
      cessationCompleted: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/cessation/confirm/summary`),
    );
  });

  it('should return to emissions page', async () => {
    store.setState({
      ...mockTaskState,
      isEditable: true,
      cessation: {
        determinationOutcome: 'APPROVED',
        annualReportableEmissions: 22,
        subsistenceFeeRefunded: false,
        noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
        notes: 'my notes',
      },
      allowancesSurrenderRequired: false,
      cessationCompleted: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/cessation/confirm/emissions`),
    );
  });

  it('should return to previous step', async () => {
    activatedRouteSnapshot.data = { statusKey: 'REVOCATION_APPLY', keys: ['effectiveDate'] };
    store.setState(mockTaskState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/cessation/confirm/outcome`),
    );
  });

  it('should redirect to answers page', async () => {
    store.setState({
      ...mockTaskState,
      isEditable: false,
      cessation: {
        determinationOutcome: 'APPROVED',
        annualReportableEmissions: 22,
        subsistenceFeeRefunded: false,
        noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
        notes: 'my notes',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/cessation/confirm/answers`),
    );
  });

  it('should return true', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: {
        state: {
          changing: true,
        },
      },
    } as any);

    store.setState({
      ...mockTaskState,
      isEditable: false,
      cessation: {
        determinationOutcome: 'APPROVED',
        annualReportableEmissions: 22,
        subsistenceFeeRefunded: false,
        noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
        notes: 'my notes',
      },
    });

    await expect(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>).toEqual(true);
  });
});
