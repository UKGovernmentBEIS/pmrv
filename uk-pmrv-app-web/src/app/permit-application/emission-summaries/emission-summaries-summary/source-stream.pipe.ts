import { Pipe, PipeTransform } from '@angular/core';

import { SourceStream } from 'pmrv-api';

@Pipe({ name: 'sourceStream' })
export class SourceStreamPipe implements PipeTransform {
  transform(sourceStreams: SourceStream[], sourceStreamId: string): SourceStream {
    return sourceStreams.find((stream) => stream.id === sourceStreamId);
  }
}
