## Form identifier service

The `FormService` implements generators for form element ids.
Govuk components consume the generated id automatically.
`ErrorSummaryComponent` automatically links errors to their field using the generated id.

The default implementation uses a control's path to calculate the id, and concatenates its parts with a dot (`.`).

It can be overridden by providing another `FormService` in a module, which overrides `getControlIdentifier` and `getIdentifier`.

### Examples

Override by concatenating path parts with a hyphen:

```Typescript
// form-identifier.service.ts
import { Injectable } from '@angular/core';

import { FormService } from 'govuk-components';

@Injectable()
export class FormIdentifierService extends FormService {

  getIdentifier(path: string[]): string {
    return path.join('-');
  }
}

// app.module.ts
import { NgModule } from '@angular/core';

import { GovukComponentsModule, FormService } from 'govuk-components';

import { FormIdentifierService } from './form-identifier.service';

const keycloakService = new KeycloakService();

@NgModule({
  imports: [GovukComponentsModule],
  providers: [{ provide: FormService, useClass: FormIdentifierService }],
})
export class AppModule {
}
```
