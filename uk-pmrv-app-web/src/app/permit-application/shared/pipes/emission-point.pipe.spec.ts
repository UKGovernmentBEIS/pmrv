import { TestBed } from '@angular/core/testing';

import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { EmissionPointPipe } from './emission-point.pipe';

describe('EmissionPointPipe', () => {
  let pipe: EmissionPointPipe;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionPointPipe],
    });
  });

  beforeEach(() => (pipe = new EmissionPointPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should find an emission point by id', () => {
    expect(
      pipe.transform(mockPermitApplyPayload.permit.emissionPoints, mockPermitApplyPayload.permit.emissionPoints[0].id),
    ).toEqual(mockPermitApplyPayload.permit.emissionPoints[0]);
  });
});
