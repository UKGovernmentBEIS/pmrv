import {
  AfterContentInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChildren,
  ElementRef,
  EventEmitter,
  OnDestroy,
  OnInit,
  Output,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { ActivatedRoute, NavigationStart, Router } from '@angular/router';

import { filter, Subscription, tap } from 'rxjs';

import { ScrollService } from '../scroll/scroll.service';
import { TabDirective } from './tab/tab.directive';
import { TabBaseDirective } from './tab/tab-base.directive';
import { TabLazyDirective } from './tab/tab-lazy.directive';

@Component({
  selector: 'govuk-tabs',
  templateUrl: './tabs.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TabsComponent implements OnInit, AfterContentInit, OnDestroy {
  @ContentChildren(TabBaseDirective, { descendants: false }) tabList: QueryList<TabBaseDirective>;
  @ContentChildren(TabDirective, { descendants: false }) tabEagerList: QueryList<TabDirective>;
  @ContentChildren(TabLazyDirective, { descendants: false }) tabLazyList: QueryList<TabLazyDirective>;
  @ViewChildren('anchor') anchorList: QueryList<ElementRef<HTMLAnchorElement>>;
  @Output() readonly selectedTab: EventEmitter<string> = new EventEmitter();

  private shouldFocusAnchor: boolean;
  private subscriptions = new Subscription();

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly cdRef: ChangeDetectorRef,
    private readonly _: ScrollService,
  ) {}

  ngOnInit(): void {
    this.subscriptions.add(
      this.router.events
        .pipe(
          filter((event) => event instanceof NavigationStart && event.navigationTrigger === 'popstate'),
          tap(() => (this.shouldFocusAnchor = true)),
        )
        .subscribe(),
    );
  }

  ngAfterContentInit(): void {
    this.subscriptions.add(this.route.fragment.subscribe((fragment) => this.setTargetTab(fragment)));
    this.subscriptions.add(
      this.tabList.changes.subscribe(() => {
        this.setTargetTab(this.route.snapshot.fragment);
        this.cdRef.detectChanges();
      }),
    );
    this.tabList.map((tab) =>
      this.subscriptions.add(
        tab.isSelected.subscribe((value) => {
          if (value) {
            this.selectedTab.emit(tab.id);
          }
        }),
      ),
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  anchorKeydown(event: KeyboardEvent, index: number): void {
    let targetTab;

    switch (event.key) {
      case 'ArrowLeft':
      case 'ArrowUp':
        targetTab = this.tabList.find((_, i) => i === index - 1);
        break;
      case 'ArrowRight':
      case 'ArrowDown':
        targetTab = this.tabList.find((_, i) => i === index + 1);
        break;
    }

    if (targetTab) {
      this.shouldFocusAnchor = true;
      this.router.navigate(['.'], {
        fragment: targetTab.id,
        queryParamsHandling: 'preserve',
        state: this.getState(),
        relativeTo: this.route,
      });
    }
  }

  getState() {
    return { ...window.history.state, scrollSkip: true };
  }

  private setTargetTab(fragment: string): void {
    if (this.tabList.length === 0) {
      return;
    }

    const currentTab = this.tabList.find((tab) => tab.isSelected.getValue());
    const targetTab = this.tabList.find((tab) => tab.id === fragment);
    if (!targetTab) {
      if (!currentTab) {
        this.tabList.first.isSelected.next(true);
      }
      this.shouldFocusAnchor = false;
      return;
    }

    if (currentTab) {
      currentTab.isSelected.next(false);
    }
    targetTab.isSelected.next(true);
    targetTab.cdRef.detectChanges();

    if (this.shouldFocusAnchor) {
      this.shouldFocusAnchor = false;
      this.anchorFocus(targetTab);
    }
  }

  private anchorFocus(targetTab: TabBaseDirective) {
    const targetAnchor = this.anchorList?.find(
      (_, i) => i === this.tabList.toArray().indexOf(targetTab),
    )?.nativeElement;
    if (targetAnchor && targetAnchor !== document.activeElement) {
      targetAnchor.focus();
    }
  }
}
