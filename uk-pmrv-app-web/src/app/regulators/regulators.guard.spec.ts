import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { RegulatorAuthoritiesService } from 'pmrv-api';

import { MockType } from '../../testing';
import { RegulatorsGuard } from './regulators.guard';

describe('RegulatorsGuard', () => {
  let guard: RegulatorsGuard;

  const response = { caUsers: [{ userId: 'test1' }, { userId: 'test2' }], editable: false };
  const regulatorAuthoritiesService: MockType<RegulatorAuthoritiesService> = {
    getCaRegulatorsUsingGET: jest.fn().mockReturnValue(of(response)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [RegulatorsGuard, { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService }],
    });
    guard = TestBed.inject(RegulatorsGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should resolve and return regulators in dependence offshore CA', async () => {
    const regulatorAuthoritiesServiceSpy = jest.spyOn(regulatorAuthoritiesService, 'getCaRegulatorsUsingGET');
    await lastValueFrom(guard.resolve());
    expect(regulatorAuthoritiesServiceSpy).toHaveBeenCalled();
  });
});
