import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { SharedModule } from '../../shared/shared.module';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { CancelApplicationComponent } from './cancel-application.component';

describe('CancelApplicationComponent', () => {
  let component: CancelApplicationComponent;
  let fixture: ComponentFixture<CancelApplicationComponent>;
  let store: InstallationAccountApplicationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CancelApplicationComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [InstallationAccountApplicationStore, { provide: TasksService, useValue: {} }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CancelApplicationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    store = TestBed.inject(InstallationAccountApplicationStore);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should clear store', () => {
    const storeSpy = jest.spyOn(store, 'reset');
    const element: HTMLElement = fixture.nativeElement;
    const deleteButton = element.querySelector('button');

    deleteButton.click();

    expect(storeSpy).toHaveBeenCalled();
  });
});
