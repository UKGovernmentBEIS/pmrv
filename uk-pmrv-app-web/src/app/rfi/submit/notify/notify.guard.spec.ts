import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { RfiStore } from '../../store/rfi.store';
import { NotifyGuard } from './notify.guard';

describe('NotifyGuard', () => {
  let router: Router;
  let guard: NotifyGuard;
  let store: RfiStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 123 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    guard = TestBed.inject(NotifyGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(RfiStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be true if questions is complete', async () => {
    store.setState({
      ...store.getState(),
      rfiSubmitPayload: {
        ...store.getState().rfiSubmitPayload,
        questions: ['who', 'what'],
        deadline: '01/01/2023',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to questions if questions is missing', async () => {
    store.setState({
      ...store.getState(),
      rfiSubmitPayload: {
        ...store.getState().rfiSubmitPayload,
        questions: ['who', 'what'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/questions`));
  });

  it('should redirect to answers if information is complete', async () => {
    store.setState({
      ...store.getState(),
      rfiSubmitPayload: {
        ...store.getState().rfiSubmitPayload,
        questions: ['who', 'what'],
        deadline: '01/01/2023',
        operators: ['operator'],
        signatory: 'signatory',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/answers`));
  });
});
