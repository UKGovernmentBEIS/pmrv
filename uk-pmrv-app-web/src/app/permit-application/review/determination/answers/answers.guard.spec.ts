import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewStateBuild } from '../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let guard: AnswersGuard;
  let router: Router;
  let store: PermitApplicationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276 };
  activatedRouteSnapshot.data = {
    statusKey: 'determination',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if determination is filled and task not completed ', async () => {
    store.setState(
      mockReviewStateBuild(
        {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
        },
        {
          determination: false,
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to determination if determination is not filled', async () => {
    store.setState(
      mockReviewStateBuild(
        {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: undefined,
        },
        {
          determination: false,
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/276/review/determination`));
  });

  it('should redirect to summary if task is complete and not changing', async () => {
    store.setState(
      mockReviewStateBuild(
        {
          type: 'GRANTED',
          reason: 'reason',
          activationDate: '1-1-2030',
        },
        {
          determination: true,
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/276/review/determination/summary`));
  });
});
