import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable, switchMap } from 'rxjs';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import {
  CalculationSourceStreamCategoryAppliedTier,
  FallbackSourceStreamCategoryAppliedTier,
  MeasSourceStreamCategoryAppliedTier,
  N2OSourceStreamCategoryAppliedTier,
  PFCSourceStreamCategoryAppliedTier,
} from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { CategoryTypeNamePipe } from './category-type-name.pipe';
import { FindSourceStreamPipe } from './find-source-stream.pipe';

@Pipe({ name: 'sourceStreamCategoryName' })
export class SourceStreamCategoryNamePipe implements PipeTransform {
  constructor(
    private readonly store: PermitApplicationStore,
    private findSourceStreamPipe: FindSourceStreamPipe,
    private categoryTypeNamePipe: CategoryTypeNamePipe,
    private sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
  ) {}

  transform(
    target:
      | number
      | N2OSourceStreamCategoryAppliedTier
      | MeasSourceStreamCategoryAppliedTier
      | FallbackSourceStreamCategoryAppliedTier
      | PFCSourceStreamCategoryAppliedTier
      | CalculationSourceStreamCategoryAppliedTier,
    path?: 'N2O' | 'MEASUREMENT' | 'FALLBACK' | 'PFC' | 'CALCULATION',
  ): Observable<string> {
    return typeof target === 'number'
      ? this.store
          .findTask<
            | N2OSourceStreamCategoryAppliedTier[]
            | MeasSourceStreamCategoryAppliedTier[]
            | PFCSourceStreamCategoryAppliedTier[]
            | CalculationSourceStreamCategoryAppliedTier[]
          >(`monitoringApproaches.${path}.sourceStreamCategoryAppliedTiers`)
          .pipe(switchMap((tiers) => this.transformName(tiers?.[target])))
      : this.transformName(target);
  }

  private transformName(
    tier:
      | N2OSourceStreamCategoryAppliedTier
      | MeasSourceStreamCategoryAppliedTier
      | FallbackSourceStreamCategoryAppliedTier
      | PFCSourceStreamCategoryAppliedTier
      | CalculationSourceStreamCategoryAppliedTier,
  ): Observable<string> {
    return this.findSourceStreamPipe.transform(tier?.sourceStreamCategory?.sourceStream).pipe(
      map((sourceStream) => {
        if (sourceStream) {
          return `${sourceStream.reference} ${this.sourceStreamDescriptionPipe.transform(
            sourceStream,
          )}: ${this.categoryTypeNamePipe.transform(tier?.sourceStreamCategory?.categoryType)}`;
        } else {
          return tier?.sourceStreamCategory?.categoryType ?? false
            ? 'UNDEFINED: ' + this.categoryTypeNamePipe.transform(tier.sourceStreamCategory.categoryType)
            : 'Add a source stream category';
        }
      }),
    );
  }
}
