import { TestBed } from '@angular/core/testing';

import { lastValueFrom, of } from 'rxjs';

import { VerificationBodiesService } from 'pmrv-api';

import { MockType } from '../../testing';
import { mockVerificationBodies } from './testing/mock-data';
import { VerificationBodiesResolver } from './verification-bodies.resolver';

describe('VerificationBodiesResolver', () => {
  let guard: VerificationBodiesResolver;
  const verificationBodiesService: MockType<VerificationBodiesService> = {
    getVerificationBodiesUsingGET: jest.fn().mockReturnValue(of({ verificationBodies: mockVerificationBodies })),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        VerificationBodiesResolver,
        { provide: VerificationBodiesService, useValue: verificationBodiesService },
      ],
    });
    guard = TestBed.inject(VerificationBodiesResolver);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return a list of verification bodies', async () => {
    jest.spyOn(verificationBodiesService, 'getVerificationBodiesUsingGET');
    await lastValueFrom(guard.resolve());
    expect(verificationBodiesService.getVerificationBodiesUsingGET).toHaveBeenCalled();
  });
});
