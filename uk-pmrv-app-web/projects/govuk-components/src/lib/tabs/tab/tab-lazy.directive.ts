import { Directive } from '@angular/core';

import { TabBaseDirective } from './tab-base.directive';

@Directive({
  selector: 'ng-template[govukTabLazy]',
  providers: [{ provide: TabBaseDirective, useExisting: TabLazyDirective }],
})
export class TabLazyDirective extends TabBaseDirective {}
