## Summary List

The summary list component is a visible container used to summarise information, for example a user's response at the end of a form.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/summary-list/).

The summary list component accepts templates for keys and values. Provide the templates with `keyTemplate` and `valueTemplate` selectors.
The context of the template is set to implicitly (default) include the `SummaryItem` and the `index`. If any of these two templates
is detected, it will be used instead of the default bare text.

A reminder that the row will not render if the value is `null`, `undefined` or empty string.

Alternatively, you can also provide your own content for the summary list with the help of `div[govukSummaryListRow]`, `dt[govukSummaryListRowKey]` and
`dd[govukSummaryListRowValue]` directive selectors. If the component detects any `SummaryListRowDirective`, it will render the content and ignore the
`details` input.

### Inputs

- `title` - The summary list title.
- `changeRoute` - The route for the change anchor in the top of summary list.
- `details` - The summary details.
- `hasBorders` - Controls whether to display the borders of the summary list. Defaults to `true`.

### Examples

```html
<dl govuk-summary-list [details]="[{ key: 'Name', value: 'John Doe' }]"></dl>
```

```html
<dl govuk-summary-list [details]="[{ key: 'Name', value: 'John Doe' }]">
  <ng-template #keyTemplate let-item>
    <h3>Example template with header</h3>
    <div>This will be rendered to the left column</div>
    <div>Item key: {{item.key}}</div>
  </ng-template>
  <ng-template #valueTemplate let-item let-i="index">
    <div>
      You have access to both the key and the value of each item so you make a combined view or any other operation
    </div>
    <div>Key: {{item.key}}, with value: {{item.value}} and index: {{i}}</div>
  </ng-template>
</dl>
```

```html
<dl govuk-summary-list>
  <div govukSummaryListRow *ngFor="let row of yourRecordsArray">
    <dt govukSummaryListRowKey>You can work here using any angular logic.</dt>
    <dd govukSummaryListRowValue>You can work here using any angular logic.</dd>
  </div>
  <div govukSummaryListRow>
    <dt govukSummaryListRowKey>Another row key.</dt>
    <dd govukSummaryListRowValue>Another row value.</dd>
  </div>
</dl>
```
