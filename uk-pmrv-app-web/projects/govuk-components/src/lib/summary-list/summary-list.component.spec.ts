import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SummaryListRowDirective, SummaryListRowKeyDirective, SummaryListRowValueDirective } from './directives';
import { SummaryListComponent } from './summary-list.component';

describe('SummaryListComponent', () => {
  let component1: SummaryListComponent;
  let component2: SummaryListComponent;
  let component3: SummaryListComponent;
  let fixture1: ComponentFixture<DetailsTestComponent>;
  let fixture2: ComponentFixture<DetailsTemplateTestComponent>;
  let fixture3: ComponentFixture<TemplateTestComponent>;

  @Component({
    template: '<dl govuk-summary-list [details]="details"></dl>',
  })
  class DetailsTestComponent {
    details = [
      { key: 'key 1', value: 'value 1' },
      { key: 'key 2', value: 'value 2' },
    ];
  }

  @Component({
    template: `
      <dl govuk-summary-list [details]="details">
        <ng-template #keyTemplate let-item>
          <div>Item key: {{ item.key }}</div>
        </ng-template>
        <ng-template #valueTemplate let-item let-i="index">
          <div>Key: {{ item.key }}, with value: {{ item.value }} and index: {{ i }}</div>
        </ng-template>
      </dl>
    `,
  })
  class DetailsTemplateTestComponent {
    details = [
      { key: 'key 1', value: 'value 1' },
      { key: 'key 2', value: 'value 2' },
    ];
  }

  @Component({
    template: `
      <dl govuk-summary-list [details]="details">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Test first key {{ details[0].key }}</dt>
          <dd govukSummaryListRowValue>Test first value {{ details[0].value }}</dd>
        </div>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Test second key {{ details[1].key }}</dt>
          <dd govukSummaryListRowValue>Test second value {{ details[1].value }}</dd>
        </div>
      </dl>
    `,
  })
  class TemplateTestComponent {
    details = [
      { key: 'key 1', value: 'value 1' },
      { key: 'key 2', value: 'value 2' },
    ];
  }

  const getElementArray: (fixture: ComponentFixture<any>, selector: string) => string[] = (fixture, selector) =>
    Array.from(fixture.nativeElement.querySelectorAll(selector)).map((element) =>
      (element as HTMLElement).textContent.trim(),
    );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        SummaryListComponent,
        DetailsTestComponent,
        DetailsTemplateTestComponent,
        TemplateTestComponent,
        SummaryListRowDirective,
        SummaryListRowKeyDirective,
        SummaryListRowValueDirective,
      ],
      imports: [RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture1 = TestBed.createComponent(DetailsTestComponent);
    fixture2 = TestBed.createComponent(DetailsTemplateTestComponent);
    fixture3 = TestBed.createComponent(TemplateTestComponent);
    component1 = fixture1.debugElement.query(By.directive(SummaryListComponent)).componentInstance;
    component2 = fixture2.debugElement.query(By.directive(SummaryListComponent)).componentInstance;
    component3 = fixture3.debugElement.query(By.directive(SummaryListComponent)).componentInstance;
    fixture1.detectChanges();
    fixture2.detectChanges();
    fixture3.detectChanges();
  });

  it('should create', () => {
    expect(component1).toBeTruthy();
    expect(component2).toBeTruthy();
    expect(component3).toBeTruthy();
  });

  it('should have two rows', () => {
    expect(fixture1.nativeElement.querySelectorAll('.govuk-summary-list__row').length).toEqual(2);
    expect(fixture2.nativeElement.querySelectorAll('.govuk-summary-list__row').length).toEqual(2);
    expect(fixture3.nativeElement.querySelectorAll('.govuk-summary-list__row').length).toEqual(2);
  });

  it('should display the values correctly', () => {
    expect(getElementArray(fixture1, '.govuk-summary-list__key')).toContain('key 1');
    expect(getElementArray(fixture1, '.govuk-summary-list__key')).toContain('key 2');
    expect(getElementArray(fixture1, '.govuk-summary-list__value')).toContain('value 1');
    expect(getElementArray(fixture1, '.govuk-summary-list__value')).toContain('value 2');

    expect(getElementArray(fixture2, '.govuk-summary-list__key')).toContain('Item key: key 1');
    expect(getElementArray(fixture2, '.govuk-summary-list__key')).toContain('Item key: key 2');
    expect(getElementArray(fixture2, '.govuk-summary-list__value')).toContain(
      'Key: key 1, with value: value 1 and index: 0',
    );
    expect(getElementArray(fixture2, '.govuk-summary-list__value')).toContain(
      'Key: key 2, with value: value 2 and index: 1',
    );

    expect(getElementArray(fixture3, '.govuk-summary-list__key')).toContain('Test first key key 1');
    expect(getElementArray(fixture3, '.govuk-summary-list__key')).toContain('Test second key key 2');
    expect(getElementArray(fixture3, '.govuk-summary-list__value')).toContain('Test first value value 1');
    expect(getElementArray(fixture3, '.govuk-summary-list__value')).toContain('Test second value value 2');
  });
});
