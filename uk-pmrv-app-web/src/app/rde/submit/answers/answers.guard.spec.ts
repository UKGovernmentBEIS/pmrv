import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { RdeStore } from '../../store/rde.store';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let guard: AnswersGuard;
  let router: Router;
  let store: RdeStore;

  const routerStateSnapshot = {
    url: '/rde/123/notify-users',
  } as RouterStateSnapshot;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 123 };
  activatedRouteSnapshot.url = [new UrlSegment('notify-users', null)];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(RdeStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be true if information is complete', async () => {
    store.setState({
      ...store.getState(),
      rdePayload: {
        ...store.getState().rdePayload,
        extensionDate: '10/10/2026',
        deadline: '08/10/2026',
        operators: ['users'],
        signatory: 'assignees',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to extend determination if information is missing', async () => {
    store.setState({
      ...store.getState(),
      rdePayload: {
        ...store.getState().rdePayload,
        extensionDate: '10/10/2026',
        deadline: '08/10/2026',
        operators: ['users'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rde/${activatedRouteSnapshot.params.taskId}/extend-determination`));
  });
});
