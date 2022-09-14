import { TestBed } from '@angular/core/testing';

import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { EmissionSourcePipe } from './emission-source.pipe';

describe('EmissionSourcePipe', () => {
  let pipe: EmissionSourcePipe;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionSourcePipe],
    });
  });

  beforeEach(() => (pipe = new EmissionSourcePipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should find an emission source by id', () => {
    expect(
      pipe.transform(
        mockPermitApplyPayload.permit.emissionSources,
        mockPermitApplyPayload.permit.emissionSources[0].id,
      ),
    ).toEqual(mockPermitApplyPayload.permit.emissionSources[0]);
  });
});
