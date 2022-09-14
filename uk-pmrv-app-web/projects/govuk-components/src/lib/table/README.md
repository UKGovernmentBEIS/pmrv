## Table component

The table component renders a table following the GOV.UK design standards.
Design details can be found at [GOV.UK Design System](https://design-system.service.gov.uk/components/table/).

If an `<ng-template>` is provided as component content, then it is used as a cell template.
Its context consists of the current `row` and `column` variables.

### Inputs

- `data` - An array of objects of any type.
- `columns` - An array of objects following the `GovukTableColumn` interface. The `field` property is used to determine which value of each object will be rendered.
  The `isHeader` property is used to determine if the column will be considered a row header.
- `caption` - A string to be used as a caption.

### Outputs

- `sort` - An event emitted whenever a sortable column is clicked.
  The event object contains a column property, with the respective column field,
  as well as a direction property with the sorting direction.

### Example

```html
<govuk-table [columns]="columns" [data]="data" (sort)="onSort($event)">
  <ng-template let-column="column" let-row="row">
    <a [routerLink]="row.url">{{row[column.field]}}</a>
  </ng-template>
</govuk-table>
```

```typescript
columns: GovukTableColumn[] = [
    { header: 'Name', field: 'name', widthClass: 'govuk-!-width-one-quarter', isHeader: true },
    { header: 'Surname', field: 'surname'},
    { header: 'Age', field: 'age', isSortable: true }
]

data: any[] = [
    { name: 'Name 1', surname: 'Surname 1', age: 23 },
    { name: 'Name 2', surname: 'Surname 2', age: 48 },
    { name: 'Name 3', surname: 'Surname 3', age: 32 },
]

onSort({ column, direction }: SortEvent) {
 this.data = this.data.slice().sort((a, b) => direction === 'ascending' ? a[column] - b[column] : b[column] - a[column]);
}
```
