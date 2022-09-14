## Radio component

Radio component is designed as a custom form control that follows the govuk style guidelines.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/radios/).

The radio component supports conditional content for options. We can achieve that by providing content inside
each `govuk-radio-option`, marked with the `govukConditionalContent` directive.
Child controls are automatically disabled when the content is hidden.

### Inputs

- `legend` - A heading as a title.
- `legendSize` - A `LegendSizeType`. Defaults to `medium`.
- `hint` - A hint with smaller font as a description.
- `radioSize` - Defaults to `large`. Also accepts `medium`.
- `isInline` - A boolean flag controlling whether the radio button will be inline or not. Defaults to `false`.

`govuk-radio-option`:

- `value` - The option value.
- `label` - The option text.
- `hint` - an optional hint text.

### Examples

```html
<form [formGroup]="exampleForm">
  <div govuk-radio formControlName="exampleControlName"></div>
</form>
```

```html
<div govuk-radio [formControl]="exampleControl"></div>
```

```html
<div govuk-radio [formControl]="exampleControl">
  <govuk-radio-option label="Example 1" [value]="1">
    <ng-container govukConditionalContent>Example conditional content for option 1</ng-container>
  </govuk-radio-option>
  <govuk-radio-option label="Example 2" [value]="2"></govuk-radio-option>
  <govuk-radio-option label="Example 3" [value]="3">
    <ng-container govukConditionalContent>Example conditional content for option 3</ng-container>
  </govuk-radio-option>
</div>
```
