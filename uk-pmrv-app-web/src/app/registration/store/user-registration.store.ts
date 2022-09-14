import { Injectable } from '@angular/core';

import { Store } from '../../core/store';
import { initialState, UserRegistrationState } from './user-registration.state';

@Injectable({ providedIn: 'root' })
export class UserRegistrationStore extends Store<UserRegistrationState> {
  constructor() {
    super(initialState);
  }
}
