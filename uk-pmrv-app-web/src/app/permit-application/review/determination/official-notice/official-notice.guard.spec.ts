import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockReviewStateBuild } from '../../../testing/mock-state';
import { OfficialNoticeGuard } from './official-notice.guard';

describe('OfficialNoticeGuard', () => {
  let guard: OfficialNoticeGuard;
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
    guard = TestBed.inject(OfficialNoticeGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should activate if task is in progress and not changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockReviewStateBuild({
        type: 'REJECTED',
        reason: 'reason',
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to determination if type is missing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockReviewStateBuild({
        determination: false,
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/276/review/determination`));
  });

  it('should redirect to answers if task needs review and not changing ', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockReviewStateBuild(
        {
          type: 'REJECTED',
          reason: 'reason',
          officialNotice: 'official notice',
        },
        {
          determination: false,
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/276/review/determination/answers`));
  });

  it('should redirect to summary if task is complete and not changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockReviewStateBuild(
        {
          type: 'REJECTED',
          reason: 'reason',
          officialNotice: 'official notice',
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
