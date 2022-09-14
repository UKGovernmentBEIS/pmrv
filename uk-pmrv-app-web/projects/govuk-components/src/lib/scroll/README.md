## Scroll service

Scroll service implements the default scroll restoration behavior found here [Angular router ExtraOptions](https://angular.io/api/router/ExtraOptions).
The added functionality is the use of a navigation state parameter called `skipScroll` that can be used to abort the scroll (actually overriding the
scroll and navigating to the same position).

### Examples

Inject the service and use the state parameter

```typescript
constructor(private readonly _: ScrollService) {}
```

```typescript
this.router.navigate([targetRoute], { fragment: targetFragment, state: { scrollSkip: true } });
```

```html
<a [routerLink]="targetRoute" [fragment]="targetFragment" [state]="{ scrollSkip: true }">
```
