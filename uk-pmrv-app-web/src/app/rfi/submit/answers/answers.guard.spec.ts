import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { RfiStore } from '../../store/rfi.store';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let router: Router;
  let guard: AnswersGuard;
  let store: RfiStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 123 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(RfiStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be true if information is complete', async () => {
    store.setState({
      ...store.getState(),
      rfiSubmitPayload: {
        questions: ['who', 'what'],
        deadline: '01/01/2023',
        operators: ['operator'],
        signatory: 'signatory',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to questions if information is missing', async () => {
    store.setState({
      ...store.getState(),
      rfiSubmitPayload: {
        ...store.getState().rfiSubmitPayload,
        questions: ['who', 'what'],
        deadline: '01/01/2023',
        operators: ['operator'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/questions`));
  });
});
