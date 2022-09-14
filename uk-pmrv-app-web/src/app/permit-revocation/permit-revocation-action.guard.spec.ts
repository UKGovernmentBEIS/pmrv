import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { MockType } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { PermitRevocationActionGuard } from './permit-revocation-action.guard';

describe('PermitRevocationActionGuard', () => {
  let guard: PermitRevocationActionGuard;

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionByIdUsingGET: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(PermitRevocationActionGuard);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
