import { Injectable } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class IdentityBarService {
  content = new BehaviorSubject<string>(null);
  data = new BehaviorSubject<any>(null);

  show(content: string, data?: any): void {
    this.content.next(content);
    this.data.next(data);
  }

  hide(): void {
    this.content.next(null);
    this.data.next(null);
  }
}
