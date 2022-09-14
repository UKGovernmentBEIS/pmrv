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
  activatedRouteSnapshot.data = { statusKey: 'REVOCATION_APPLY', keys: ['reason'] };

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
      permitRevocation: {
        reason: 'Because i have to',
        activitiesStopped: true,
        stoppedDate: '2022-04-14',
        effectiveDate: '2022-05-16',
        surrenderRequired: false,
        annualEmissionsReportRequired: true,
        annualEmissionsReportDate: '2022-04-14',
        feeCharged: false,
      },
      sectionsCompleted: {
        REVOCATION_APPLY: true,
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/apply/summary`));
  });

  it('should return to previous step', async () => {
    activatedRouteSnapshot.data = { statusKey: 'REVOCATION_APPLY', keys: ['effectiveDate'] };
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...mockTaskState.permitRevocation,
        reason: 'some reason',
        activitiesStopped: true,
      },
      sectionsCompleted: {},
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/apply/reason`));
  });

  it('should redirect to answers page', async () => {
    store.setState({
      ...mockTaskState,
      isEditable: false,
      permitRevocation: {
        reason: 'Because i have to',
        activitiesStopped: true,
        stoppedDate: '2022-04-14',
        effectiveDate: '2022-05-16',
        surrenderRequired: false,
        annualEmissionsReportRequired: true,
        annualEmissionsReportDate: '2022-04-14',
        feeCharged: false,
      },
      sectionsCompleted: {
        REVOCATION_APPLY: false,
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/apply/answers`));
  });

  it('should return true when navigated in changing state', async () => {
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
      permitRevocation: {
        reason: 'Because i have to',
        activitiesStopped: true,
        stoppedDate: '2022-04-14',
        effectiveDate: '2022-05-16',
        surrenderRequired: false,
        annualEmissionsReportRequired: true,
        annualEmissionsReportDate: '2022-04-14',
        feeCharged: false,
      },
      sectionsCompleted: {
        REVOCATION_APPLY: false,
      },
    });

    await expect(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>).toEqual(true);
  });
});
