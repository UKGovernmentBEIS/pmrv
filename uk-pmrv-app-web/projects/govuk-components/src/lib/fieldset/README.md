## Fieldset

Use the fieldset component to group related form inputs.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/fieldset/).

All parts are implemented as Angular attribute directives.
The legend element must be the first element of the fieldset, followed by an optional span hint element.

### Inputs

`govukFieldset`:

- `id` - A unique id for the fieldset.

`govukLegend`:

- `size` - Adjusts the legend size. Defaults to medium.

### Example

```html
<fieldset govukFieldset id="field">
  <legend govukLegend size="heading">
    <h1>Title</h1>
  </legend>
  <div>
    <label>Some field</label>
    <input />
  </div>
</fieldset>
```

```html
<fieldset govukFieldset>
  <legend govukLegend size="large">Object <span class="govuk-visually-hidden">help</span></legend>
  <span govukFieldsetHint>Your name is useful in our survey.</span>
  <div>
    <label>Some field</label>
    <input />
  </div>
</fieldset>
```
