import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { RecallGuard } from './recall.guard';

describe('RecallGuard', () => {
  let guard: RecallGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276, index: 0 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
      providers: [KeycloakService],
    });
    store = TestBed.inject(CommonTasksStore);

    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS'],
      },
    });

    router = TestBed.inject(Router);
    guard = TestBed.inject(RecallGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should  activate if prerequisites are met', async () => {
    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if action is not allowed', async () => {
    store.setState({
      ...store.getState(),
      requestTaskItem: {
        ...store.getState().requestTaskItem,
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/permit-notification/follow-up/wait`),
    );
  });
});
