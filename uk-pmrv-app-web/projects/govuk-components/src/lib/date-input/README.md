## Date input

Use the date input component to help users enter a memorable date or one they can easily look up. This component was created as a custom
form control to be integrated with Angular's reactive forms. Design details can be found
at [GOV.UK Design System](https://design-system.service.gov.uk/components/date-input/).

### Inputs

- `label` - A label for the input.
- `hint` - A hint displayed beneath the label.
- `min` - The min date value will be used to validate the control against a minimum date.
- `max` - The max date value will be used to validate the control against a maximum date.

### Examples

```html
<form [formGroup]="exampleForm">
  <div govuk-date-input label="exampleLabel" formControlName="exampleFormControlName" hint="exampleHint"></div>
</form>
```

```html
<div
  govuk-date-input
  label="exampleLabel"
  [formControl]="exampleFormControl"
  hint="exampleHint"
  [min]="exampleMinDate"
  [max]="exampleMaxDate"
></div>
```
