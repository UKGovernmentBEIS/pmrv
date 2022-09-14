## File upload component

The file upload component was designed as a custom form control to be integrated with Angular's reactive forms. The form control value will
be set to the `FileList` selected. Design details can be found
at [GOV.UK Design System](https://design-system.service.gov.uk/components/file-upload/)

### Inputs

- `label` - A label for the input. Defaults to `Upload a file`.
- `accepted` - Accepted exposes the native input property `accept`.
- `isMultiple` - Exposing the native `multiple` property.

### Examples

```html
<form [formGroup]="exampleForm">
  <div govukFileUpload label="exampleLabel" formControlName="exampleFormControlName"></div>
</form>
```

```html
<div govukFileUpload label="exampleLabel" [formControl]="exampleFormControl"></div>
```
