## Breadcrumbs

The breadcrumbs component helps users to understand where they are within a website’s structure and move between levels.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/breadcrumbs/).

Only anchor links decorated with `govukLink="breadcrumb"` should be used as the component's content.
These links form the breadcrumb hierarchy, from higher to lower.

### Example

```html
<govuk-breadcrumbs>
  <a govukLink="breadcrumb" href="#">Home</a>
  <a govukLink="breadcrumb" href="#">Travel abroad</a>
  <a govukLink="breadcrumb" href="#">Environment</a>
</govuk-breadcrumbs>
```
