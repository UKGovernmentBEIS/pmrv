## Select

Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/select/).

### Inputs

- `label` - A label for the input.
- `options` - An array of objects having `text` property as option label and `value` property as option value.
- `widthClass` - A class to be used for the select. Accepts the following Gov.UK standards:
  - `govuk-!-width-full`
  - `govuk-!-width-three-quarters`
  - `govuk-!-width-two-thirds`
  - `govuk-!-width-one-half`
  - `govuk-!-width-one-third`
  - `govuk-!-width-one-quarter`

### Examples

```html
<form [formGroup]="exampleForm">
  <div
    govuk-select
    label="exampleLabel"
    formControlName="exampleFormControlName"
    [options]="[{text: 'One', value: 'one'}, {text: 'Two', value: 'two'}]"
  >
    <option [ngValue]="{ prop1: 'asd', prop2: 23 }">A third one</option>
    <option value="four">A fourth one</option>
  </div>
</form>
```
