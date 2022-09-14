## Textarea component

The textarea component was designed as a custom form control to be integrated with Angular's reactive forms.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/textarea/)
and at [GOV.UK Design System](https://design-system.service.gov.uk/components/character-count/)

### Inputs

- `label` - A label for the input.
- `hint` - A hint displayed beneath the label.
- `rows` - Exposing the native textarea element `rows` property.
- `maxLength` - Providing a maxlength numeric value is needed to transform the component to include a character count.

### Examples

```html
<form [formGroup]="exampleForm">
  <div govuk-textarea label="exampleLabel" formControlName="exampleFormControlName" hint="exampleHint"></div>
</form>
```

```html
<div govuk-textarea label="exampleLabel" [formControl]="exampleFormControl" hint="exampleHint"></div>
```
