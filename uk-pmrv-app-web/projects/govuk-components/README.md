# GOV.UK Frontend (Angular)

An implementation of the [GOV.UK Design System](https://design-system.service.gov.uk/)
in [Angular](https://angular.io/).

This angular library purpose is to track the
[GOV.UK Frontend](https://github.com/alphagov/govuk-frontend) project
as to which components to implement and how they should look and behave.
Where possible the existing CSS is being used.

## Components

Below you can find a list of the implemented [GDS components](https://design-system.service.gov.uk/components/) so far:

- [Accordion](src/lib/accordion/README.md)
- [Back link](src/lib/back-link/README.md)
- [Breadcrumbs](src/lib/breadcrumbs/README.md)
- [Button](src/lib/directives/button/README.md)
- Character count (implemented as part of the [textarea](src/lib/textarea/README.md) component)
- [Checkboxes](src/lib/checkboxes/README.md)
- [Date input](src/lib/date-input/README.md)
- [Details](src/lib/details/README.md)
- [Error message](src/lib/error-message)
- [Error summary](src/lib/error-summary/README.md)
- [Fieldset](src/lib/fieldset/README.md)
- [File upload](src/lib/file-upload/README.md)
- [Footer](src/lib/footer/README.md)
- [Header](src/lib/header/README.md)
- [Inset text](src/lib/directives/inset-text/README.md)
- [Panel](src/lib/panel/README.md)
- [Phase banner](src/lib/phase-banner/README.md)
- [Radios](src/lib/radio/README.md)
- [Select](src/lib/select/README.md)
- [Skip link](src/lib/skip-link/README.md)
- [Summary list](src/lib/summary-list/README.md)
- [Table](src/lib/table/README.md)
- [Tabs](src/lib/tabs/README.md)
- [Tag](src/lib/tag/README.md)
- [Text input](src/lib/text-input/README.md)
- [Textarea](src/lib/textarea/README.md)
- [Warning text](src/lib/warning-text/README.md)

## Usage

To use the library you should first build the project:

```shell script
yarn build:govuk-components
```

After the project builds successfully you must import the `GovukComponentsModule`
into the module you plan to use a relevant component:

```js
import { GovukComponentsModule } from 'govuk-components';

@NgModule({
  imports: [GovukComponentsModule],
})
export class SomeModule {}
```

## Development

To develop new features for the library you can run:

```shell script
yarn start:govuk-components
```

This command will start the library in watch mode which will run a new build
when a file changes.

### Running tests

To run unit tests, run `yarn test:govuk-components`. To run unit tests with
coverage report, run `yarn test:govuk-components:coverage`.

To run lint, run `yarn lint`
