## Error summary

Use this component at the top of a page to summarise any errors a user has made.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/error-summary/).

The component displays all control error messages belonging to a form,
and creates anchor fragment links to them, given that their id follows the Angular control path, i.e. `parent.child` id format.
E.g. if in the example below, the first control's id should be `ground.material`, while the array's first
should be `vehicles.0`.
all `govuk-components` form components automatically assign such identifiers.

### Inputs

- `form` - The form group acting as root for the errors. Its child form controls will be traversed and linked for errors.

### Example

```html
<form [formGroup]="form">
  <govuk-error-summary [form]="form"></govuk-error-summary>

  <div formGroupName="ground">
    <div govuk-text-input formControlName="material" label="Material"></div>
  </div>

  <div formArrayName="vehicles">
    <div govuk-text-input *ngFor="let vehicle of vehicles.controls; let i = index" [formControlName]="i"></div>
  </div>
</form>
```
