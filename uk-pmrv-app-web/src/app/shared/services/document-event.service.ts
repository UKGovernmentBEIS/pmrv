import { Inject, Injectable, RendererFactory2 } from '@angular/core';
import { InjectionToken } from '@angular/core';

import { Subject } from 'rxjs';

export const DOCUMENT_EVENT = new InjectionToken<Subject<PointerEvent | FocusEvent>>('Document event', {
  providedIn: 'root',
  factory: () => new Subject<PointerEvent | FocusEvent>(),
});

@Injectable({ providedIn: 'root' })
export class DocumentEventService {
  constructor(
    @Inject(DOCUMENT_EVENT) private readonly documentEvent$: Subject<PointerEvent | FocusEvent>,
    private rendererFactory2: RendererFactory2,
  ) {
    this.init();
  }

  init(): void {
    const renderer = this.rendererFactory2.createRenderer(null, null);

    renderer.listen('document', 'click', (event: PointerEvent) => this.documentEvent$.next(event));
    renderer.listen('document', 'focusin', (event: FocusEvent) => this.documentEvent$.next(event));
  }
}
