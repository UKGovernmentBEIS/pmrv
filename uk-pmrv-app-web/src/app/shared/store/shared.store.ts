import { Injectable } from '@angular/core';

import { Store } from '../../core/store';
import { initialState, SharedState } from './shared.state';

@Injectable({ providedIn: 'root' })
export class SharedStore extends Store<SharedState> {
  constructor() {
    super(initialState);
  }
  setState(state: SharedState): void {
    super.setState(state);
  }
}
