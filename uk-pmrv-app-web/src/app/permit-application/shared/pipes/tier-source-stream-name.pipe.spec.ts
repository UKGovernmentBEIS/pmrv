import { TestBed } from '@angular/core/testing';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { mockClass } from '@testing';

import { N2OMonitoringApproach } from 'pmrv-api';

import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { CategoryTypeNamePipe } from './category-type-name.pipe';
import { TierSourceStreamNamePipe } from './tier-source-stream-name.pipe';

describe('TierSourceStreamNamePipe', () => {
  let pipe: TierSourceStreamNamePipe;

  const sourceStreamDescriptionPipe = mockClass(SourceStreamDescriptionPipe);
  const categoryTypeNamePipe = mockClass(CategoryTypeNamePipe);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TierSourceStreamNamePipe],
      providers: [
        { provide: SourceStreamDescriptionPipe, useValue: sourceStreamDescriptionPipe },
        { provide: CategoryTypeNamePipe, useValue: categoryTypeNamePipe },
      ],
    });

    pipe = new TierSourceStreamNamePipe(sourceStreamDescriptionPipe, categoryTypeNamePipe);
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform tier source stream and category', () => {
    sourceStreamDescriptionPipe.transform.mockReturnValueOnce('ss_descr');
    categoryTypeNamePipe.transform.mockReturnValueOnce('category_name');

    const mockedSousreStream = mockPermitApplyPayload.permit.sourceStreams[0];
    const mockedTierCategory = (mockPermitApplyPayload.permit.monitoringApproaches.N2O as N2OMonitoringApproach)
      .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

    expect(pipe.transform(mockedSousreStream, mockedTierCategory)).toEqual(
      `${mockedSousreStream.reference} ss_descr: category_name`,
    );

    expect(sourceStreamDescriptionPipe.transform).toHaveBeenCalledTimes(1);
    expect(categoryTypeNamePipe.transform).toHaveBeenCalledTimes(1);
    expect(sourceStreamDescriptionPipe.transform).toHaveBeenCalledWith(mockedSousreStream);
    expect(categoryTypeNamePipe.transform).toHaveBeenCalledWith(mockedTierCategory.categoryType);
  });
});
