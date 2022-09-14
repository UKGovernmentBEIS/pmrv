import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { ActivatedRouteSnapshotStub } from '../../../../../testing';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let guard: AnswersGuard;
  let store: SharedStore;

  const route = new ActivatedRouteSnapshotStub({ taskId: '111' }, {}, { path: 'permit-application' });
  const state = {
    url: '/permit-application/279/review/peer-review-decision/answers',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRouteSnapshot, useValue: route },
        { provide: RouterStateSnapshot, useValue: state },
      ],
    });
    guard = TestBed.inject(AnswersGuard);
    store = TestBed.inject(SharedStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if information is missing', async () => {
    await expect(firstValueFrom(guard.canActivate(route, state))).resolves.toEqual(
      TestBed.inject(Router).parseUrl('/permit-application/111/review/peer-review-decision'),
    );
  });

  it('should activate if information exists', async () => {
    store.setState({
      ...store.getState(),
      peerReviewDecision: {
        type: 'AGREE',
        notes: 'I agree',
      },
    });

    await expect(firstValueFrom(guard.canActivate(route, state))).resolves.toEqual(true);
  });
});
