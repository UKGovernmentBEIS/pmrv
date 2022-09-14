import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { InherentCO2Component } from './inherent-co2.component';
import { InherentCo2Module } from './inherent-co2.module';

describe('InherentCO2Component', () => {
  let component: InherentCO2Component;
  let fixture: ComponentFixture<InherentCO2Component>;
  let page: Page;
  let store: PermitApplicationStore;

  class Page extends BasePage<InherentCO2Component> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get subTasks(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li'));
    }
    getTaskStatus(content) {
      return Array.from(this.subTasks)
        .find((li) => li.querySelector<HTMLSpanElement>('span').textContent.trim() === content)
        .querySelector<HTMLElement>('govuk-tag strong')
        .textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InherentCo2Module, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub({ taskId: '123' }) }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InherentCO2Component);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of tasks', () => {
    expect(page.getTaskStatus('Approach description')).toEqual('not started');
    expect(page.summaryListValues).toEqual([]);
  });

  it('should display the task summary', () => {
    store.setState({
      ...store.getState(),
      permitSectionsCompleted: { ...store.payload.permitSectionsCompleted, INHERENT_CO2_Description: [true] },
    });
    fixture.detectChanges();

    expect(page.getTaskStatus('Approach description')).toEqual('completed');
    expect(page.summaryListValues).toEqual([['Approach description', 'test']]);
  });
});
