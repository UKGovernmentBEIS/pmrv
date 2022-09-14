import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PaginationComponent } from './pagination.component';

describe('PaginationComponent', () => {
  let component: PaginationComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;
  let route: ActivatedRoute;

  @Component({
    template: `
      <app-pagination
        [count]="count"
        [pageSize]="pageSize"
        (currentPageChange)="this.currentPage = $event"
      ></app-pagination>
    `,
  })
  class TestComponent {
    count;
    pageSize;
    currentPage;
  }

  @Component({ template: '<router-outlet></router-outlet>' })
  class RouterComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: '', component: TestComponent }])],
      declarations: [PaginationComponent, TestComponent, RouterComponent],
    });
  });

  beforeEach(() => {
    TestBed.createComponent(RouterComponent);
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(PaginationComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    element = fixture.nativeElement;
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should calculate total pages', () => {
    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item')).length).toEqual(1);

    hostComponent.count = 36;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item')).length).toEqual(5);
    expect(component.pageNumbers).toEqual([1, 2, 3, 4]);

    hostComponent.count = 53;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item')).length).toEqual(7);
    expect(component.pageNumbers).toEqual([1, 2, 3, 4, 5, 6]);
  });

  it('should emit currentPage', async () => {
    const links = element.querySelectorAll<HTMLLIElement>('.hmcts-pagination__item--active');
    expect(hostComponent.currentPage).toEqual(1);
    expect(links.length).toEqual(1);
    expect(links[0].textContent).toEqual('1');

    hostComponent.count = 36;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    const page3 = fixture.debugElement.queryAll(By.css('.hmcts-pagination__link'))[1].nativeElement;
    expect(page3.textContent.trim()).toEqual('3');

    page3.click();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(route.snapshot.queryParamMap.get('page')).toEqual('3');
    expect(hostComponent.currentPage).toEqual(3);
  });

  it('should show dots for a large amount of pages', async () => {
    hostComponent.count = 126;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item--dots')).length).toEqual(1);
    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item')).length).toEqual(5);
  });

  it('should limit page size to the current count', () => {
    hostComponent.count = 3;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    const getDetails = () =>
      Array.from(element.querySelector('.hmcts-pagination__results').querySelectorAll('strong')).map(
        (text) => text.textContent,
      );

    expect(getDetails()).toEqual(['1', '3', '3']);

    hostComponent.count = 15;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    fixture.debugElement.queryAll(By.css('.hmcts-pagination__link'))[1].nativeElement.click();
    fixture.detectChanges();

    expect(getDetails()).toEqual(['11', '15', '15']);
  });

  it('should not show previous on first page or next on last page', () => {
    hostComponent.count = 30;
    hostComponent.pageSize = 10;
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item--prev'))).toHaveLength(0);
    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item--next'))).toHaveLength(1);

    fixture.debugElement.queryAll(By.css('.hmcts-pagination__link'))[0].nativeElement.click();
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item--prev'))).toHaveLength(1);
    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item--next'))).toHaveLength(1);

    fixture.debugElement.queryAll(By.css('.hmcts-pagination__link'))[2].nativeElement.click();
    fixture.detectChanges();

    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item--prev'))).toHaveLength(1);
    expect(fixture.debugElement.queryAll(By.css('.hmcts-pagination__item--next'))).toHaveLength(0);
  });

  it('should display at least one page if no results exist', () => {
    hostComponent.count = 0;
    hostComponent.pageSize = 0;
    fixture.detectChanges();

    const links = element.querySelectorAll('.hmcts-pagination__item');

    expect(links.length).toEqual(1);
    expect(links[0].textContent).toEqual('1');
  });
});
