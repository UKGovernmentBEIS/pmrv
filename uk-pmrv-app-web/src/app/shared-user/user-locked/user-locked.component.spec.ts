import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserLockedComponent } from './user-locked.component';

describe('UserLockedComponent', () => {
  let component: UserLockedComponent;
  let fixture: ComponentFixture<UserLockedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserLockedComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserLockedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
