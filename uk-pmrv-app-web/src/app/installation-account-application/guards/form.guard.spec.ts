import { TestBed } from '@angular/core/testing';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom } from 'rxjs';

import { cloneDeep } from 'lodash-es';

import { AccountsService, LegalEntitiesService, TasksService } from 'pmrv-api';

import { AccountsServiceStub } from '../../../testing/accounts.service.stub';
import { LegalEntitiesServiceStub } from '../../../testing/legal-entities.service.stub';
import { INSTALLATION_FORM, installationFormFactory } from '../factories/installation-form.factory';
import {
  LEGAL_ENTITY_FORM,
  legalEntityFormFactory,
  legalEntityInitialValue,
} from '../factories/legal-entity-form.factory';
import { ApplicationSectionType, initialState } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { mockState } from '../testing/mock-state';
import { FormGuard } from './form.guard';

describe('FormGuard', () => {
  let guard: FormGuard;
  let installationForm: FormGroup;
  let legalEntityForm: FormGroup;
  let store: InstallationAccountApplicationStore;

  const findSectionValue = (section) => mockState.tasks.find((task) => task.type === section).value;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, ReactiveFormsModule],
      providers: [
        FormGuard,
        installationFormFactory,
        legalEntityFormFactory,
        { provide: AccountsService, useClass: AccountsServiceStub },
        { provide: LegalEntitiesService, useClass: LegalEntitiesServiceStub },
        { provide: TasksService, useValue: {} },
      ],
    });
    guard = TestBed.inject(FormGuard);
    installationForm = TestBed.inject(INSTALLATION_FORM);
    legalEntityForm = TestBed.inject(LEGAL_ENTITY_FORM);
    store = TestBed.inject(InstallationAccountApplicationStore);

    [ApplicationSectionType.installation, ApplicationSectionType.legalEntity].forEach((section) =>
      store.updateTask(section, findSectionValue(section)),
    );
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should populate the application form', async () => {
    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();

    expect(installationForm.value).toMatchObject(findSectionValue(ApplicationSectionType.installation));
    expect(legalEntityForm.value).toEqual(findSectionValue(ApplicationSectionType.legalEntity));
  });

  it('should disable the installation type and location when the application is under review', async () => {
    store.setState({ ...store.getState(), isReviewed: true });

    await expect(lastValueFrom(guard.canActivate())).resolves.toBeTruthy();
    expect(installationForm.get('locationGroup').disabled).toBeTruthy();
    expect(installationForm.get('installationTypeGroup').disabled).toBeTruthy();
  });

  it('should reset the forms if no value is provided', async () => {
    await lastValueFrom(guard.canActivate());
    expect(installationForm.value).toEqual(
      expect.objectContaining(findSectionValue(ApplicationSectionType.installation)),
    );
    expect(legalEntityForm.value).toEqual(findSectionValue(ApplicationSectionType.legalEntity));

    const emptyState = cloneDeep(initialState);
    store.setState({ ...emptyState });
    await lastValueFrom(guard.canActivate());

    expect(installationForm.get('locationGroup').value.location).toBeNull();
    expect(installationForm.get('installationTypeGroup').value.type).toBeNull();

    expect(legalEntityForm.get('selectGroup').value.isNew).toEqual(legalEntityInitialValue.selectGroup?.isNew);
    expect(legalEntityForm.get('selectGroup').value.id).toBeNull();
    expect(legalEntityForm.get('detailsGroup').value.type).toBeNull();
    expect(legalEntityForm.get('detailsGroup').value.name).toBeNull();
  });
});
