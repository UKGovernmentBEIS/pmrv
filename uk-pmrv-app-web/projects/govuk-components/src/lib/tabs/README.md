## Tabs component

Tabs component lets users navigate between related sections of content, displaying one section at a time
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/tabs/).

The tabs component **must** contain at least one tab.
This component supports eager load with the `ng-template[govukTab]` directive, lazy load with the `ng-template[govukTabLazy]` directive
and async operations.

### Inputs

Tab item inputs:

- `id` - The tab item id
- `label` - The tab item label

### Examples

_Example 1:_ A tab component with 3 tabs

```html
<govuk-tabs>
  <ng-template govukTab id="example1" label="Example 1"> This is eager loaded </ng-template>
  <ng-template govukTabLazy id="lazy" label="Lazy"> This is lazy loaded </ng-template>
  <ng-template govukTab id="example2" label="Example 2"> This is eager loaded </ng-template>
</govuk-tabs>
```

Example 2: A tab component where the tabs are rendered using an `*ngFor` iteration in an asynchronous way via `async`

```html
<govuk-tabs>
  <ng-template govukTab *ngFor="let tab of tabs$ | async" [id]="tab.id" [label]="tab.label"> {{tab.body}} </ng-template>
  <ng-template govukTabLazy id="lazy" label="Lazy"> This is lazy loaded </ng-template>
  <ng-template govukTabLazy *ngFor="let tab of tabsLazy$ | async" [id]="tab.id" [label]="tab.label">
    {{tab.body}}
  </ng-template>
  <ng-template govukTab id="eager" label="Eager"> This is eager loaded </ng-template>
</govuk-tabs>
```

where `tabs$` and `tabsLazy`:

```js
tabs$ = of([
  { id: 'asyncEager1', label: 'Async Eager 1', body: 'Async Eager 1 content' },
  { id: 'asyncEager2', label: 'Async Eager 2', body: 'Async Eager 2 content' },
]).pipe(delay(2000));
tabsLazy$ = of([
  { id: 'asyncLazy1', label: 'Async Lazy 1', body: 'Async Lazy 1 content' },
  { id: 'asyncLazy2', label: 'Async Lazy 2', body: 'Async Lazy 2 content' },
]).pipe(delay(2000));
```
