## Text input component

The text input component was designed as a custom form control to be integrated with Angular's reactive forms. Design details can be found
at [GOV.UK Design System](https://design-system.service.gov.uk/components/text-input/).

Providing `number` as a value in the `inputType` field will emit a numeric value instead of text and will activate numeric functionalities
such as `not NaN` validation and format(while not focused) if provided. For available number formats,
study [Angular Decimal Pipe](https://angular.io/api/common/DecimalPipe).

### Inputs

- `label` - A label for the input.
- `hint` - A hint displayed beneath the label.
- `inputType` - Exposing the native input element `type` property. Defaults to `text`.
- `autoComplete` - Exposing the native input element `autocomplete` property. Defaults to `on`.
- `inputMode` - Exposing the native input element `inputmode` property.
- `numberFormat` - Only applicable when `number` is given to the `inputType` field. Provide a format for the numbers to appear in while not
  focused. Eg. `1.0-0`
- `spellCheck` - Exposing the native input element `spellcheck` property.
- `prefix` - A text to be displayed before the field.
- `suffix` - A text to be displayed after the field.
- `widthClass` - A class to be used for the input. Accepts the following Gov.UK standards:
  - `govuk-!-width-full` - default.
  - `govuk-!-width-three-quarters`
  - `govuk-!-width-two-thirds`
  - `govuk-!-width-one-half`
  - `govuk-!-width-one-third`
  - `govuk-!-width-one-quarter`
  - `govuk-input--width-20`
  - `govuk-input--width-10`
  - `govuk-input--width-5`
  - `govuk-input--width-4`
  - `govuk-input--width-3`
  - `govuk-input--width-2`

### Examples

```html
<form [formGroup]="exampleForm">
  <div govuk-text-input label="exampleLabel" formControlName="exampleFormControlName" hint="exampleHint"></div>
</form>
```

```html
<div
  govuk-text-input
  label="exampleLabel"
  [formControl]="exampleFormControl"
  widthClass="govuk-!-width-three-quarters"
  hint="exampleHint"
></div>
```

```html
<div govuk-text-input [formControl]="exampleFormControl">
  <ng-container govukLabel>Object <span class="govuk-visually-hidden">help</span></ng-container>
</div>
```
