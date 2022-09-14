import { FormControl } from '@angular/forms';

import { MessageValidatorFn } from 'govuk-components';

export type GroupBuilderConfig<T, K extends keyof T = keyof T> = Partial<
  Record<K, [T[K] | { value: T[K]; disabled: boolean }, (MessageValidatorFn[] | MessageValidatorFn)?] | FormControl>
>;
