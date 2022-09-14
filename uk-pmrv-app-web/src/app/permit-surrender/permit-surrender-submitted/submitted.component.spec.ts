import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { SubmittedComponent } from './submitted.component';

@Component({
  selector: 'app-permit-surrender-summary',
  template: 'Mocked Summary component',
})
class MockSummaryComponent {}

describe('SubmittedComponent', () => {
  let component: SubmittedComponent;
  let fixture: ComponentFixture<SubmittedComponent>;
  let hostElement: HTMLElement;
  let page: Page;

  class Page extends BasePage<SubmittedComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    hostElement = fixture.nativeElement;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmittedComponent, MockSummaryComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render surrender values with files', () => {
    createComponent();
    expect(page.heading.textContent).toEqual('Surrender your permit');
    expect(hostElement.textContent).toContain('Mocked Summary component');
  });
});
