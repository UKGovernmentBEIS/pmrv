import { Pipe, PipeTransform } from '@angular/core';

import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import {
  FallbackSourceStreamCategory,
  MeasSourceStreamCategory,
  N2OSourceStreamCategory,
  SourceStream,
} from 'pmrv-api';

import { CategoryTypeNamePipe } from './category-type-name.pipe';

@Pipe({
  name: 'tierSourceStreamName',
})
export class TierSourceStreamNamePipe implements PipeTransform {
  constructor(
    private sourceStreamDescriptionPipe: SourceStreamDescriptionPipe,
    private categoryTypeNamePipe: CategoryTypeNamePipe,
  ) {}

  transform(
    sourceStream: SourceStream,
    tierCategory: N2OSourceStreamCategory | MeasSourceStreamCategory | FallbackSourceStreamCategory,
  ): string {
    return `${sourceStream.reference} ${this.sourceStreamDescriptionPipe.transform(
      sourceStream,
    )}: ${this.categoryTypeNamePipe.transform(tierCategory?.categoryType)}`;
  }
}
