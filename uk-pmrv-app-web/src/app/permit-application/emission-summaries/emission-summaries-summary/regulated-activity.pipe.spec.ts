import { TestBed } from '@angular/core/testing';

import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { RegulatedActivityPipe } from './regulated-activity.pipe';

describe('RegulatedActivityPipe', () => {
  let pipe: RegulatedActivityPipe;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegulatedActivityPipe],
    });
  });

  beforeEach(() => (pipe = new RegulatedActivityPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should find an emission source by id', () => {
    expect(
      pipe.transform(
        mockPermitApplyPayload.permit.regulatedActivities,
        mockPermitApplyPayload.permit.regulatedActivities[0].id,
      ),
    ).toEqual(mockPermitApplyPayload.permit.regulatedActivities[0]);
  });
});
