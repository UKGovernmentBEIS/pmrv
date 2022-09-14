import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { LegalEntitiesService, LegalEntityDTO } from 'pmrv-api';

import { BasePage, changeInputValue, CountryServiceStub, MockType } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { LegalEntitySelect } from '../../shared/interfaces/legal-entity';
import { SharedModule } from '../../shared/shared.module';
import { legalEntityFormFactory } from '../factories/legal-entity-form.factory';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { mockPayload } from '../testing/mock-state';
import { LegalEntitySelectComponent } from './legal-entity-select.component';

const value: LegalEntitySelect = {
  isNew: true,
  id: null,
};

describe('LegalEntitySelectComponent', () => {
  let component: LegalEntitySelectComponent;
  let fixture: ComponentFixture<LegalEntitySelectComponent>;
  let page: Page;
  let router: Router;
  let legalEntitiesService: MockType<LegalEntitiesService>;
  let store: InstallationAccountApplicationStore;

  const legalEntity: LegalEntityDTO = { ...mockPayload.legalEntity, id: 1 };
  let navigateSpy: jest.SpyInstance<Promise<boolean>>;

  class Page extends BasePage<LegalEntitySelectComponent> {
    get optionValues() {
      return this.queryAll<HTMLInputElement>('input[name="isNew"]').map((option) => this.getInputValue(option));
    }

    get idValue() {
      return this.getInputValue('select[name="id"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    legalEntitiesService = { getLegalEntityByIdUsingGET: jest.fn().mockReturnValue(of(legalEntity)) };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [LegalEntitySelectComponent],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: LegalEntitiesService, useValue: legalEntitiesService },
        legalEntityFormFactory,
        InstallationAccountApplicationStore,
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    TestBed.inject(ActivatedRoute).data = of({ legalEntities: [{ id: 1, name: 'Legal Entity' }] });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LegalEntitySelectComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(InstallationAccountApplicationStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from setState', () => {
    component.form.patchValue(value);
    fixture.detectChanges();

    fixture.detectChanges();
    expect(page.optionValues).toEqual([false, true]);

    component.form.patchValue({ isNew: false, id: 2 });
    fixture.detectChanges();

    expect(page.optionValues).toEqual([true, false]);
    expect(page.idValue).toEqual('2');
  });

  it('should not submit if not new and id is null', () => {
    component.form.patchValue({ ...value, id: null });

    changeInputValue(fixture, '#isNew-option0');

    page.submitButton.click();

    expect(navigateSpy).not.toHaveBeenCalled();

    changeInputValue(fixture, '#id', 1);
    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should navigate to details if new', () => {
    changeInputValue(fixture, '#isNew-option1');
    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../details'], { relativeTo: TestBed.inject(ActivatedRoute) });
    expect(legalEntitiesService.getLegalEntityByIdUsingGET).not.toHaveBeenCalled();
  });

  it('should update task if existing and then move to task list', () => {
    const updateTaskSpy = jest.spyOn(store, 'updateTask').mockImplementation();
    changeInputValue(fixture, '#isNew-option0');
    changeInputValue(fixture, '#id', 1);
    page.submitButton.click();
    fixture.detectChanges();

    expect(updateTaskSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: TestBed.inject(ActivatedRoute) });
    expect(legalEntitiesService.getLegalEntityByIdUsingGET).toHaveBeenCalled();
  });
});
