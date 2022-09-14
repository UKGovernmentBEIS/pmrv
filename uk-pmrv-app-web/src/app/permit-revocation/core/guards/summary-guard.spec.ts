import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { SummaryGuard } from './summary-guard';

describe('Summary Guard', () => {
  let router: Router;
  let guard: SummaryGuard;
  let store: PermitRevocationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 13 };
  activatedRouteSnapshot.data = { statusKey: 'REVOCATION_APPLY' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitRevocationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true when section status is completed', async () => {
    store.setState({
      ...store.getState(),
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

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      true,
    );
  });

  it('should redirect to the first step of the wizard', async () => {
    store.setState({
      ...mockTaskState,
      permitRevocation: {
        ...mockTaskState.permitRevocation,
        reason: 'some reason',
      },
      sectionsCompleted: {},
    });
    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/apply/reason`),
    );
  });

  it('should return true when user has view only rights', async () => {
    store.setState({
      ...mockTaskState,
      isEditable: false,
      permitRevocation: {
        ...mockTaskState.permitRevocation,
        reason: 'some reason',
      },
      sectionsCompleted: {
        REVOCATION_APPLY: false,
      },
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      true,
    );
  });
});
