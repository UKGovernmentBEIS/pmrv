## Checkboxes component

Checkboxes component is designed as a custom form control that follows the govuk style guidelines. Design details can be
found at [GOV.UK Design System](https://design-system.service.gov.uk/components/checkboxes/).

The checkboxes component supports conditional content for options. We can achieve that by providing content inside
each `govuk-checkbox`, marked with the `govukConditionalContent` directive.
Child controls are automatically disabled when the content is hidden.

### Inputs

- `legend` - A heading as a title
- `hint` - A hint with smaller font as a description
- `size` - Defaults to `null`. Also accepts `small`
- `legendSize` - Adjusts the legend size. Defaults to `large`. Accepts `normal` | `medium` | `large` | `heading`

`govuk-checkbox`:

- `value` - The option value.
- `label` - The option text.
- `hint` - an optional hint text.

### Examples

```html
<form [formGroup]="exampleForm">
  <div govuk-checkboxes formControlName="exampleControlName" legend="exampleLegend"></div>
</form>
```

```html
<div govuk-checkboxes [formControl]="exampleControl">
  <ng-container govukLegend>Object <span class="govuk-visually-hidden">help</span></ng-container>
</div>
```

```html
<div govuk-checkboxes [formControl]="exampleControl">
  <govuk-checkbox [value]="1" label="Example 1">
    <ng-container govukConditionalContent>Example conditional content for option 1</ng-container>
  </govuk-checkbox>
  <govuk-checkbox [value]="2" label="Example 2"></govuk-checkbox>
  <govuk-checkbox [value]="3">
    <ng-container govukLabel>Example 3</ng-container>
    <ng-container govukConditionalContent>Example conditional content for option 3</ng-container>
  </govuk-checkbox>
</div>
```
