import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitCessation } from 'pmrv-api';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockActionCompletedState } from '../testing/mock-state';
import { CompletedComponent } from './completed.component';

describe('CompletedComponent', () => {
  @Component({
    selector: 'app-cessation-summary-details',
    template: '<p>Mock cessation summary details</p>',
  })
  class MockCessationSummaryDetailsComponent {
    @Input() cessation: PermitCessation;
    @Input() allowancesSurrenderRequired: boolean;
    @Input() isEditable: boolean;
  }

  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;
  let page: Page;

  let store: PermitSurrenderStore;

  class Page extends BasePage<CompletedComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get subHeadings() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    get titles() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dt')).map((dt) => dt.textContent.trim()),
      );
    }
    get values() {
      return this.queryAll<HTMLDListElement>('dl').map((el) =>
        Array.from(el.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompletedComponent, MockCessationSummaryDetailsComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  it('should create', () => {
    store.setState(mockActionCompletedState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should display headings', () => {
    store.setState(mockActionCompletedState);
    createComponent();
    expect(page.heading.textContent).toEqual('Cessation completed');
    expect(page.subHeadings.map((subheading) => subheading.textContent)).toEqual([
      'Details',
      'Official notice recipients',
    ]);
  });

  it('should display recipients', () => {
    store.setState(mockActionCompletedState);
    createComponent();
    expect(page.titles).toEqual([['Users', 'Name and signature on the official notice', 'Official notice']]);
    expect(page.values).toEqual([
      ['Operator1 Name - Primary contact  Operator2 Name - Primary contact', 'Regulator Name', 'off notice.pdf'],
    ]);
  });
});
