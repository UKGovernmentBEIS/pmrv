import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { SharedModule } from '../../../../shared/shared.module';
import { ActionLayoutComponent } from '../action-layout/action-layout.component';
import { BaseActionContainerComponent } from './base-action-container.component';

describe('BaseTaskContainerComponent', () => {
  let component: BaseActionContainerComponent;
  let fixture: ComponentFixture<BaseActionContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService],
      declarations: [BaseActionContainerComponent, ActionLayoutComponent],
      imports: [SharedModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BaseActionContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
