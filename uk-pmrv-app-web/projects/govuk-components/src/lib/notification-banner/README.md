## Notification banner

A notification banner lets you tell the user about something that’s not directly relevant to the thing they’re trying to
do on that page of the service. Design details can be found
at [GOV.UK Design System](https://design-system.service.gov.uk/components/notification-banner/).

Any content can be projected within the component, and will be displayed as the notification banner body.
All links within the notification banner body should be decorated with the `govuk-notification-banner__link` class, instead of `govuk-link`.

### Inputs

- `type` - The notification banner heading.
- `heading` - An optional custom heading for the notification banner.

### Example

```html
<govuk-notification-banner [type]="type" [heading]="heading">
  <h3 class="govuk-notification-banner__heading">Training outcome recorded and trainee withdrawn</h3>
  <p class="govuk-body">
    Contact <a govukLink="notification" href="#">example@department.gov.uk</a> if you think there's a problem.
  </p>
</govuk-notification-banner>
```
