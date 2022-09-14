The button directive can be used on native `<button>` or `<a>` elements
to enhance them with the [GDS Button](https://design-system.service.gov.uk/components/button/)
styling.

Native `<button>` and `<a>` elements are always used to provide the most straightforward
and accessible experience for users. A `<button>` element should be used whenever
some action can be performed like starting an application or saving their information.
An `<a>` element should be used whenever the user will navigate to another view.

### Options

- `govukButton`: [Default button](https://design-system.service.gov.uk/components/button/#default-buttons) styling.
  Used for the main call to action on a page. Applicable on both `<a>` and `<button>` elements.
- `govukSecondaryButton`: [Secondary button](https://design-system.service.gov.uk/components/button/#secondary-buttons) styling. Used for secondary
  calls to action on a page. Applicable **only** `<button>` elements.
- `govukWarnButton`: [Warning button](https://design-system.service.gov.uk/components/button/#warning-buttons)
  styling. Used for actions with serious destructive consequences that cannot be easily undone by a user.
  Applicable on both `<a>` and `<button>` elements.

The following is an example of valid `<button>` and `<a>` elements:

```angular2html
<button type="button" govukButton>Default button</button>
<button type="button" govukSecondaryButton>Secondary button</button>
<button type="button" govukWarnButton>Warn button</button>

<a href="#" govukButton>Default button link</a>
<a href="#" govukWarnButton>Warn button link</a>
```

### Debounce click

Sometimes, users double click action buttons for several reasons:

- have used operating systems where they have to double click items
  to make them work
- are experiencing a slow connection which means they are not given
  feedback on their action quickly enough
- have motor impairments such as hand tremors which cause them to click
  the button involuntarily

In some cases this can mean information is sent twice. To handle this issue a
debounce click directive is implemented.

#### Options

- `debounceTime`: time to wait till the handle of next click
- `debounceClick`: emits the mouse event

Here is an example of how to use it:

```angular2html
<button type="submit" govukButton govukDebounceClick
        [debounceTime]="1000"
        (debounceClick)="doSomething($event)">
    Submit
</button>
```
