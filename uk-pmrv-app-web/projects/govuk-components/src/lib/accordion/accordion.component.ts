import {
  AfterContentInit,
  ChangeDetectionStrategy,
  Component,
  ContentChildren,
  Inject,
  Input,
  OnInit,
  QueryList,
} from '@angular/core';

import { combineLatest, map, Observable, startWith, switchMap, take } from 'rxjs';

import { ACCORDION, Accordion, accordionFactory } from './accordion';
import { AccordionItemComponent } from './accordion-item/accordion-item.component';

@Component({
  selector: 'govuk-accordion',
  template: `
    <div class="govuk-accordion" [id]="id">
      <div class="govuk-accordion__controls">
        <button
          type="button"
          class="govuk-accordion__open-all"
          [attr.aria-expanded]="areExpanded$ | async"
          (click)="toggleAllSections()"
        >
          {{ (areExpanded$ | async) ? 'Close all' : 'Open all' }}
          <span class="govuk-visually-hidden"> sections</span>
        </button>
      </div>
      <ng-content select="govuk-accordion-item"></ng-content>
    </div>
  `,
  providers: [accordionFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccordionComponent implements OnInit, AfterContentInit {
  @Input() id: string;
  @Input() openIndexes: number[];
  @Input() cacheDisabled: boolean;
  @ContentChildren(AccordionItemComponent) accordionItems: QueryList<AccordionItemComponent>;

  areExpanded$: Observable<boolean>;

  constructor(@Inject(ACCORDION) private accordion: Accordion) {}

  ngOnInit(): void {
    this.accordion.id = this.id;
    this.accordion.openIndexes = this.openIndexes;
    this.accordion.cacheDisabled = this.cacheDisabled;
  }

  ngAfterContentInit(): void {
    this.areExpanded$ = this.accordionItems.changes.pipe(
      startWith(this.accordionItems),
      switchMap((items: AccordionItemComponent[]) => combineLatest(items.map((item) => item.isExpanded$))),
      map((areExpanded) => areExpanded.every((isExpanded) => isExpanded)),
    );
  }

  toggleAllSections: () => void = () =>
    this.areExpanded$
      .pipe(take(1))
      .subscribe((areExpanded) => this.accordionItems.forEach((item) => item.isExpanded.next(!areExpanded)));
}
