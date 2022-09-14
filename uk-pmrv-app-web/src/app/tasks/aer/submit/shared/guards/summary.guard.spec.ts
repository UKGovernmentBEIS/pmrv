import { TestBed } from '@angular/core/testing';
import { Router, RouterStateSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteSnapshotStub } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { SummaryGuard } from './summary.guard';

describe('SummaryGuard', () => {
  let guard: SummaryGuard;
  let store: CommonTasksStore;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    });
    store = TestBed.inject(CommonTasksStore);
    guard = TestBed.inject(SummaryGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1' }, undefined, {
            aerTask: 'abbreviations',
          }),
          { url: '/tasks/1/aer/submit/abbreviations/summary' } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(true);

    store.setState({
      ...mockStateBuild(
        {
          abbreviations: {
            exist: false,
          },
        },
        {
          abbreviations: [false],
        },
      ),
      isEditable: false,
    });

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1' }, undefined, {
            aerTask: 'abbreviations',
          }),
          { url: '/tasks/1/aer/submit/abbreviations/summary' } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(true);
  });

  it('should not activate', async () => {
    store.setState(
      mockStateBuild(
        {
          abbreviations: {
            exist: false,
          },
        },
        {
          abbreviations: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(
        guard.canActivate(
          new ActivatedRouteSnapshotStub({ taskId: '1' }, undefined, {
            statusKey: 'abbreviations',
          }),
          {
            url: '/tasks/1/aer/submit/abbreviations/summary',
          } as RouterStateSnapshot,
        ),
      ),
    ).resolves.toEqual(TestBed.inject(Router).parseUrl('/tasks/1/aer/submit/abbreviations'));
  });
});
