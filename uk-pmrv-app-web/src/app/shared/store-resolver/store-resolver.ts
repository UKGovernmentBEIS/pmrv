import { Injectable, Injector } from '@angular/core';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { UrlRequestType } from '@shared/types/url-request-type';

import { Store } from '../../core/store';
import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { PermitSurrenderStore } from '../../permit-surrender/store/permit-surrender.store';
import { CommonTasksStore } from '../../tasks/store/common-tasks.store';

@Injectable({
  providedIn: 'root',
})
export class StoreResolver {
  constructor(private injector: Injector) {}

  getStore(path: UrlRequestType): Store<any> {
    switch (path) {
      case 'permit-application':
        return <PermitApplicationStore>this.injector.get(PermitApplicationStore);
      case 'permit-surrender':
        return <PermitSurrenderStore>this.injector.get(PermitSurrenderStore);
      case 'permit-revocation':
        return <PermitRevocationStore>this.injector.get(PermitRevocationStore);
      case 'permit-notification':
        return <CommonTasksStore>this.injector.get(CommonTasksStore);
      default:
        return null;
    }
  }
}
