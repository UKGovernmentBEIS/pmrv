import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { VerifierAuthoritiesService } from 'pmrv-api';

import { MockType } from '../../testing';
import { VerifiersGuard } from './verifiers.guard';

describe('VerifiersGuard', () => {
  let guard: VerifiersGuard;

  const response = { verifiers: [{ userId: 'test1' }, { userId: 'test2' }], editable: false };
  const verifierAuthoritiesService: MockType<VerifierAuthoritiesService> = {
    getVerifierAuthoritiesUsingGET: jest.fn().mockReturnValue(of(response)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [VerifiersGuard, { provide: VerifierAuthoritiesService, useValue: verifierAuthoritiesService }],
    });
    guard = TestBed.inject(VerifiersGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should resolve and return verifiers', async () => {
    await lastValueFrom(guard.resolve());
    expect(verifierAuthoritiesService.getVerifierAuthoritiesUsingGET).toHaveBeenCalled();
  });
});
