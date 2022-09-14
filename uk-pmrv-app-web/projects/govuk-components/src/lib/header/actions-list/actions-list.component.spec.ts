import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderActionsListComponent } from './actions-list.component';

describe('HeaderActionsListComponent', () => {
  let component: HeaderActionsListComponent;
  let fixture: ComponentFixture<HeaderActionsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HeaderActionsListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderActionsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
