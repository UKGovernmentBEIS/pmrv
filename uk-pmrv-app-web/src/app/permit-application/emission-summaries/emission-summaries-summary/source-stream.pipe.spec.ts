import { TestBed } from '@angular/core/testing';

import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { SourceStreamPipe } from './source-stream.pipe';

describe('SourceStreamPipe', () => {
  let pipe: SourceStreamPipe;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SourceStreamPipe],
    });
  });

  beforeEach(() => (pipe = new SourceStreamPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should find an emission source by id', () => {
    expect(
      pipe.transform(mockPermitApplyPayload.permit.sourceStreams, mockPermitApplyPayload.permit.sourceStreams[0].id),
    ).toEqual(mockPermitApplyPayload.permit.sourceStreams[0]);
  });
});
