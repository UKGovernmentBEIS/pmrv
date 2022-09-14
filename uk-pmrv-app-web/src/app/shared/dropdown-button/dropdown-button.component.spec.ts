import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { BasePage } from '../../../testing';
import { DropdownButtonComponent } from './dropdown-button.component';
import { DropdownButtonItemComponent } from './item/dropdown-button-item.component';

describe('MultiSelectComponent', () => {
  @Component({
    template: `
      <div app-dropdown-button>
        <div app-dropdown-button-item label="Withdraw" (actionEmit)="withdraw()"></div>
        <div app-dropdown-button-item label="Reject" (actionEmit)="reject()"></div>
      </div>
    `,
  })
  class TestComponent {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    withdraw(): void {}

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    reject(): void {}
  }

  let component: DropdownButtonComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get mainButton() {
      return this.query<HTMLButtonElement>('button.main-button');
    }
    get actionButtons() {
      return this.queryAll<HTMLButtonElement>('button.button-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent, DropdownButtonComponent, DropdownButtonItemComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(DropdownButtonComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display main button', () => {
    expect(page.mainButton).toBeTruthy();
  });

  it('should not display action buttons on load', () => {
    expect(page.actionButtons).toHaveLength(0);
  });

  it('should show action buttons upon clicking main button', () => {
    page.mainButton.click();
    fixture.detectChanges();

    expect(page.actionButtons.map((button) => button.textContent.trim())).toEqual(['Withdraw', 'Reject']);
  });

  it('should emit action event upon pressing action button', () => {
    jest.spyOn(hostComponent, 'withdraw');

    page.mainButton.click();
    fixture.detectChanges();

    const withdrawButton = page.actionButtons.find((button) => button.textContent.trim() === 'Withdraw');

    withdrawButton.click();
    fixture.detectChanges();

    expect(hostComponent.withdraw).toHaveBeenCalled();
  });
});
