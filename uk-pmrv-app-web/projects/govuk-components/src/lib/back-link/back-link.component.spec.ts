import { Location } from '@angular/common';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BackLinkComponent } from './back-link.component';

describe('BackLinkComponent', () => {
  @Component({ template: '<govuk-back-link [link]="link"></govuk-back-link>' })
  class TestComponent {
    link: string;
  }

  @Component({ template: '' })
  class RoutedComponent {}

  @Component({
    template: ` <router-outlet></router-outlet>`,
  })
  class RouterComponent {}

  let component: BackLinkComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let router: Router;
  let location: Location;
  let route: ActivatedRoute;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: 'first', component: RoutedComponent },
          { path: 'second', component: RoutedComponent },
        ]),
      ],
      declarations: [BackLinkComponent, RoutedComponent, TestComponent, RouterComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    TestBed.createComponent(RouterComponent);
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(BackLinkComponent)).componentInstance;
    router = fixture.debugElement.injector.get(Router);
    location = fixture.debugElement.injector.get(Location);
    route = fixture.debugElement.injector.get(ActivatedRoute);
    jest.spyOn(location, 'back').mockImplementation();
    jest.spyOn(router, 'navigate').mockImplementation();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should go back when no link is provided', () => {
    const element: HTMLElement = fixture.nativeElement;
    const anchor = element.querySelector('a');
    anchor.click();
    fixture.detectChanges();

    expect(location.back).toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should navigate to hard coded link', () => {
    hostComponent.link = '/something';
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    const anchor = element.querySelector('a');
    anchor.click();
    fixture.detectChanges();

    expect(location.back).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/something'], { relativeTo: route });
  });
});
